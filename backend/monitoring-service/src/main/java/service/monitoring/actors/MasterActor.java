package service.monitoring.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import core.entities.cockroachdb.BaseMonitor;
import core.entities.mongodb.MonitorLog;
import core.repostiories.cockroachdb.MonitorRepository;
import core.repostiories.mongodb.MonitorLogRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created By: Prashant Chaubey
 * Created On: 06-12-2019 00:56
 * Purpose: Master actor which will control child actors for work
 **/
public class MasterActor extends AbstractActor {
    private static Logger LOG = Logger.getLogger(MasterActor.class.toString()); //Logger object
    private MonitorRepository monitorRepository;  //Access cockroachdb
    private MonitorLogRepository monitorLogRepository; //Access mongodb
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
     * Configuration of what behavior for what message.
     *
     * @return configuration object
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder().
                match(MonitoringProtocol.FindWork.class, (obj) -> findWork()).
                match(MonitoringProtocol.AssignWork.class, (obj) -> assignWork(obj.getMonitors())).
                match(MonitoringProtocol.Wait.class, (obj) -> waitForSomeTime()).
                match(MonitoringProtocol.UpdateWork.class, (obj) -> updateLog(obj.getMonitorLog())).
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
            ActorRef child = getContext().actorOf(Props.create(WorkerActor.class, getSelf(), monitor));
            this.monitorToActor.put(monitor.getId(), child);
            //Tell it to start work
            child.tell(new MonitoringProtocol.StartWork(), getSelf());
        }
        //Message to self to wait for some time
        getSelf().tell(new MonitoringProtocol.Wait(), getSelf());
    }

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

}
