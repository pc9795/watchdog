package service.monitoring.actors;

import akka.actor.*;
import core.entities.cockroachdb.BaseMonitor;
import core.entities.mongodb.MonitorLog;
import core.repostiories.cockroachdb.MonitorRepository;
import core.repostiories.mongodb.MonitorLogRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import service.monitoring.protocols.MonitoringProtocol;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Purpose: Master actor which will control child actors for work
 **/
public class MasterActor extends AbstractActor {
    private static Logger LOG = Logger.getLogger(MasterActor.class.toString()); //Logger object
    private MonitorRepository monitorRepository;  //Access monitors
    private MonitorLogRepository monitorLogRepository; //Access monitor logs
    private int pollingInterval; //Interval at which master actor will check for work
    private Map<Long, ActorRef> monitorToActor; //Mapping of monitor id to the child actor to which work is assigned
    private int masterCount; //How many masters are working
    private int index; //What is the no of this master
    private long lastRetrievedId; //What was the last retrieved id
    private Pageable pageable; //How many results to extract from the database at a time.

    public MasterActor(MonitorRepository monitorRepository, MonitorLogRepository monitorLogRepository,
                       int pollingInterval, int masterCount, int index, int workSize) {
        this.monitorRepository = monitorRepository;
        this.monitorLogRepository = monitorLogRepository;
        this.pollingInterval = pollingInterval;
        this.monitorToActor = new HashMap<>();
        this.masterCount = masterCount;
        this.index = index;
        this.lastRetrievedId = -1;
        this.pageable = PageRequest.of(0, workSize);
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
    public static Props props(MonitorRepository monitorRepository, MonitorLogRepository monitorLogRepository,
                              int pollingInterval, int masterCount, int index, int workSize) {
        return Props.create(MasterActor.class, monitorRepository, monitorLogRepository,
                pollingInterval, masterCount, index, workSize);
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
                match(MonitoringProtocol.UpdateWork.class, obj -> updateLog(obj.getMonitorLog())).
                match(MonitoringProtocol.EditMonitorRequest.class, obj -> editWork(obj.getId(), obj.getMonitor(),
                        getSender())).
                match(MonitoringProtocol.DeleteMonitorRequest.class, obj -> deleteWork(obj.getId(), getSender())).
                match(MonitoringProtocol.StatusMasterRequest.class, obj -> status(getSender())).
                match(MonitoringProtocol.StatusWorkerRequest.class, obj -> childStatus(obj.getId(), getSender())).
                build();
    }

    /**
     * Check the cockroach db for work
     */
    private void findWork() {
        LOG.info("Finding Work...");
        List<BaseMonitor> monitors = this.monitorRepository.findWorkForMaster(this.pageable, lastRetrievedId,
                masterCount, index);
        LOG.info(String.format("Work found: %s", monitors.size()));
        //Update the latest monitor id for next query
        if (monitors.size() != 0) {
            lastRetrievedId = monitors.get(monitors.size() - 1).getId();
        }
        //Message to self to assign work
        getSelf().tell(new MonitoringProtocol.AssignWork(monitors), getSelf());
    }

    /**
     * Assign the extracted monitors to workers
     *
     * @param monitors list of monitors
     */
    private void assignWork(List<BaseMonitor> monitors) {
        LOG.info("Assigning Work...");
        for (BaseMonitor monitor : monitors) {
            //Create child actor
            ActorRef child = getContext().actorOf(WorkerActor.props(getSelf(), monitor));
            this.monitorToActor.put(monitor.getId(), child);
            //Tell it to start work
            child.tell(new MonitoringProtocol.StartWork(), getSelf());
        }
    }

    /**
     * Wait for some time before looking for work
     */
    private void waitForSomeTime() {
        LOG.info("Going to Wait...");
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
    private void updateLog(MonitorLog monitorLog) {
        LOG.info(String.format("Updating Log...%s", monitorLog.toString()));
        monitorLogRepository.save(monitorLog);
    }

    private void deleteWork(long id, ActorRef replyTo) {
        //todo uncomment
        //getContext().stop(monitorToActor.get(id));
        replyTo.tell(new MonitoringProtocol.DeleteMonitorResponse(), getSelf());
    }

    private void editWork(long id, BaseMonitor monitor, ActorRef replyTo) {
        //todo uncomment
        //getContext().stop(monitorToActor.get(id));
        //ActorRef child = getContext().actorOf(WorkerActor.props(getSelf(), monitor));
        //this.monitorToActor.put(monitor.getId(), child);
        replyTo.tell(new MonitoringProtocol.EditMonitorResponse(), getSelf());
    }

    private void status(ActorRef replyTo) {
        replyTo.tell(new MonitoringProtocol.StatusMasterResponse(this.index, this.monitorToActor.keySet()), getSelf());
    }

    private void childStatus(long id, ActorRef replyTo) {
        if (!this.monitorToActor.containsKey(id)) {
            replyTo.tell(new Status.Failure(new Exception(
                    String.format("%s is not handled by master with index:%s", id, this.index))), getSelf());
            return;
        }
        this.monitorToActor.get(id).tell(new MonitoringProtocol.StatusWorkerRequest(id, replyTo), getSelf());
    }
}
