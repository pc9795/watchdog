package service.monitoring.actors;

import akka.actor.*;
import core.beans.EmailMessage;
import core.entities.cockroachdb.BaseMonitor;
import core.entities.mongodb.MonitorLog;
import core.repostiories.cockroachdb.MonitorRepository;
import core.repostiories.mongodb.MonitorLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import service.monitoring.exceptions.ResourceNotFoundException;
import service.monitoring.protocols.MonitoringProtocol;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Purpose: Master actor which will control child actors for work
 **/
public class MasterActor extends AbstractActor {
    private static Logger LOGGER = LoggerFactory.getLogger(MasterActor.class); //Logger object
    private MonitorRepository monitorRepository;  //Access monitors
    private MonitorLogRepository monitorLogRepository; //Access monitor logs
    private int pollingInterval; //Interval at which master actor will check for work
    private Map<Long, ActorRef> monitorToActor; //Mapping of monitor id to the child actor to which work is assigned
    private int masterCount; //How many masters are working
    private int index; //What is the no of this master
    private long lastRetrievedId; //What was the last retrieved id
    private Pageable pageable; //How many results to extract from the database at a time.
    private static ArrayDeque<ActorRef> httpWorkersQueue = new ArrayDeque<>(); //Queue of http workers

    public MasterActor(MonitorRepository monitorRepository, MonitorLogRepository monitorLogRepository,
                       int pollingInterval, int masterCount, int index, int workSize, int httpWorkerPool) {
        LOGGER.warn(String.format("Actor created:%s", getSelf().toString()));
        this.monitorRepository = monitorRepository;
        this.monitorLogRepository = monitorLogRepository;
        this.pollingInterval = pollingInterval;
        this.monitorToActor = new HashMap<>();
        this.masterCount = masterCount;
        this.index = index;
        this.lastRetrievedId = -1;
        this.pageable = PageRequest.of(0, workSize);
        this.bootWorkers(httpWorkerPool);
        //Seed
        getSelf().tell(new MonitoringProtocol.FindWork(), getSelf());
    }

    /**
     * Actor configuration object
     *
     * @param monitorRepository    repostiory to access monitors
     * @param monitorLogRepository repostiory to access monitor logs
     * @param pollingInterval      interval between db requests
     * @param masterCount          no of master workers
     * @param index                index of this master
     * @param workSize             no of records to pull
     * @return configuration object
     */
    static Props props(MonitorRepository monitorRepository, MonitorLogRepository monitorLogRepository,
                       int pollingInterval, int masterCount, int index, int workSize, int httpWorkerPool) {
        return Props.create(MasterActor.class, monitorRepository, monitorLogRepository,
                pollingInterval, masterCount, index, workSize, httpWorkerPool);
    }

    /**
     * Configuration of what behavior for what message.
     *
     * @return configuration object
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder().
                match(MonitoringProtocol.FindWork.class, obj -> findWork()).
                match(MonitoringProtocol.AssignWork.class, obj -> assignWork(obj.getMonitors())).
                match(MonitoringProtocol.Wait.class, obj -> waitForSomeTime()).
                match(MonitoringProtocol.UpdateLog.class, obj -> updateLog(obj.getMonitorLog(), obj.getMonitor())).
                match(MonitoringProtocol.EditMonitorRequest.class, obj -> editWork(obj.getId(), obj.getMonitor(),
                        getSender())).
                match(MonitoringProtocol.DeleteMonitorRequest.class, obj -> deleteWork(obj.getId(), getSender())).
                match(MonitoringProtocol.StatusMasterRequest.class, obj -> status(getSender())).
                match(MonitoringProtocol.StatusWorkerRequest.class, obj -> childStatus(obj.getId(), getSender())).
                build();
    }

    /**
     * Create a queue of workers.
     *
     * @param workers no of workers
     */
    private void bootWorkers(int workers) {
        LOGGER.info(String.format("Master actor is booting %s http workers", workers));
        while (workers-- > 0) {
            httpWorkersQueue.addLast(getContext().actorOf(HttpWorkerActor.props()));
        }
    }

    /**
     * Check the cockroach db for work
     */
    private void findWork() {
        LOGGER.info("Finding Work...");
        List<BaseMonitor> monitors = this.monitorRepository.findWorkForMaster(this.pageable, lastRetrievedId,
                masterCount, index);
        LOGGER.info(String.format("Work found: %s", monitors.size()));
        //Update the latest monitor id for next query
        if (monitors.size() != 0) {
            lastRetrievedId = monitors.get(monitors.size() - 1).getId();
        }
        LOGGER.info(String.format("Updated last retrieved id:%s", lastRetrievedId));
        //Message to self to assign work
        getSelf().tell(new MonitoringProtocol.AssignWork(monitors), getSelf());
    }

    /**
     * Assign the extracted monitors to workers
     *
     * @param monitors list of monitors
     */
    private void assignWork(List<BaseMonitor> monitors) {
        LOGGER.info("Assigning Work...");
        for (BaseMonitor monitor : monitors) {
            //Create child actor
            ActorRef child = getContext().actorOf(MonitorWorkerActor.props(getSelf(), monitor));
            this.monitorToActor.put(monitor.getId(), child);
        }
        getSelf().tell(new MonitoringProtocol.Wait(), getSelf());
        LOGGER.info("Work assignment completed...");
    }

    /**
     * Wait for some time before looking for work
     */
    private void waitForSomeTime() {
        LOGGER.info("Going to Wait...");
        ActorSystem system = getContext().system();
        //Schedule a message to find work after a waiting time.
        system.scheduler().scheduleOnce(Duration.ofSeconds(this.pollingInterval), () ->
                getSelf().tell(new MonitoringProtocol.FindWork(), getSelf()), system.dispatcher());
    }

    /**
     * Handles child message to update log
     *
     * @param monitorLog monitor log object
     */
    private void updateLog(MonitorLog monitorLog, BaseMonitor monitor) {
        LOGGER.info(String.format("Updating Log...%s", monitorLog.toString()));
        monitorLogRepository.save(monitorLog);
        if (monitorLog.isWorking()) {
            return;
        }
        LOGGER.warn(String.format("Monitor is not working:%s", monitor));
        LOGGER.warn(String.format("Error message:%s", monitorLog.getErrorMessage()));
        EmailMessage message =
                new EmailMessage(monitor.getUser().getEmail(), monitor.getName(), monitorLog.getErrorMessage());
        LOGGER.warn("Going to assign an email message to an http worker");
        httpWorkersQueue.peekFirst().tell(new MonitoringProtocol.NotifyEmail(message), getSelf());
        //Round robin
        httpWorkersQueue.addLast(httpWorkersQueue.removeFirst());
    }

    /**
     * Delete worker corresponding to a given monitor id
     *
     * @param id      id of the monitor
     * @param replyTo actor to reply
     */
    private void deleteWork(long id, ActorRef replyTo) {
        if (id % this.masterCount != this.index) {
            LOGGER.info(String.format("Monitor with id:%s doesn't belong to this master with index:%s", id, this.index));
            //Not belong to this master
            return;
        }
        if (!monitorToActor.containsKey(id)) {
            //If this is the master and this work is not handled by it.
            LOGGER.warn(String.format("Monitor with id:%s should be handled by this master with index:%s but " +
                    "corresponding worker actor dosen't exists. Strange Behavior!", id, this.index));
            replyTo.tell(new Status.Failure(new ResourceNotFoundException(
                    String.format("%s is not handled by master with index:%s", id, this.index))), getSelf());
            return;
        }
        LOGGER.info(String.format("Killing child worker working on monitor with id:%s", id));
        getContext().stop(monitorToActor.get(id));
        monitorToActor.remove(id);
        LOGGER.info("Child worker deleted successfully...");
        replyTo.tell(new MonitoringProtocol.DeleteMonitorResponse(), getSelf());
    }

    /**
     * Edit the monitor assigned to the worker corresponding to given id.
     *
     * @param id      id of the monitor
     * @param monitor updated details
     * @param replyTo actor to reply
     */
    private void editWork(long id, BaseMonitor monitor, ActorRef replyTo) {
        if (id % this.masterCount != this.index) {
            LOGGER.info(String.format("Monitor with id:%s doesn't belong to this master with index:%s", id, this.index));
            //Not belong to this master
            return;
        }
        if (!monitorToActor.containsKey(id)) {
            //If this is the master and this work is not handled by it.
            LOGGER.warn(String.format("Monitor with id:%s should be handled by this master with index:%s but " +
                    "corresponding worker actor dosen't exists. Strange Behavior!", id, this.index));
            //In this case user can just delete the monitor and create new one.
            replyTo.tell(new Status.Failure(new ResourceNotFoundException(
                    String.format("%s is not handled by master with index:%s", id, this.index))), getSelf());
            return;
        }
        LOGGER.info(String.format("Forwarding request to child actor with id:%s", id));
        this.monitorToActor.get(id).tell(new MonitoringProtocol.EditMonitorRequest(id, monitor, replyTo), getSelf());
    }

    /**
     * Send the details of this monitors child workers
     *
     * @param replyTo actor to reply
     */
    private void status(ActorRef replyTo) {
        replyTo.tell(new MonitoringProtocol.StatusMasterResponse(this.index, this.monitorToActor.keySet()), getSelf());
    }

    /**
     * Forward the message to get the status corresponding child
     *
     * @param id      id of the monitor handled by the child
     * @param replyTo actor to reply
     */
    private void childStatus(long id, ActorRef replyTo) {
        if (!this.monitorToActor.containsKey(id)) {
            LOGGER.warn(String.format("Monitor with id:%s should be handled by this master with index:%s but " +
                    "corresponding worker actor dosen't exists. Strange Behavior!", id, this.index));
            replyTo.tell(new Status.Failure(new ResourceNotFoundException(
                    String.format("%s is not handled by master with index:%s", id, this.index))), getSelf());
            return;
        }
        this.monitorToActor.get(id).tell(new MonitoringProtocol.StatusWorkerRequest(id, replyTo), getSelf());
    }
}
