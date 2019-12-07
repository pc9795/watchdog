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
 * Purpose: TODO:
 **/
public class MasterActor extends AbstractActor {
    private static Logger LOG = Logger.getLogger(MasterActor.class.toString());
    private MonitorRepository monitorRepository;
    private MonitorLogRepository monitorLogRepository;
    private int pollingInterval;
    private Map<Long, ActorRef> monitorToActor;
    private int masterCount;
    private int index;
    private long lastRetrievedId;
    private Pageable pageable;

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

    @Override
    public Receive createReceive() {
        return receiveBuilder().
                match(MonitoringProtocol.FindWork.class, (obj) -> findWork()).
                match(MonitoringProtocol.AssignWork.class, (obj) -> assignWork(obj.getMonitors())).
                match(MonitoringProtocol.Wait.class, (obj) -> waitForSomeTime()).
                match(MonitoringProtocol.UpdateWork.class, (obj) -> updateLog(obj.getMonitorLog())).
                build();
    }

    private void findWork() {
        LOG.info("Finding Work...");
        List<BaseMonitor> monitors = this.monitorRepository.findWorkForMaster(this.pageable, lastRetrievedId,
                masterCount, index);
        LOG.info(String.format("Work found: %s", monitors.size()));
        if (monitors.size() != 0) {
            lastRetrievedId = monitors.get(monitors.size() - 1).getId();
        }
        getSelf().tell(new MonitoringProtocol.AssignWork(monitors), getSelf());
    }

    private void assignWork(List<BaseMonitor> monitors) {
        LOG.info("Assigning Work...");
        for (BaseMonitor monitor : monitors) {
            ActorRef child = getContext().actorOf(Props.create(WorkerActor.class, getSelf(), monitor));
            this.monitorToActor.put(monitor.getId(), child);
            child.tell(new MonitoringProtocol.StartWork(), getSelf());
        }
        getSelf().tell(new MonitoringProtocol.Wait(), getSelf());
    }

    private void waitForSomeTime() {
        LOG.info("Going to Wait...");
        ActorSystem system = getContext().system();
        system.scheduler().scheduleOnce(Duration.ofSeconds(this.pollingInterval), () -> {
            getSelf().tell(new MonitoringProtocol.FindWork(), getSelf());
        }, system.dispatcher());
    }

    private void updateLog(MonitorLog monitorLog) {
        LOG.info(String.format("Updating Log...%s", monitorLog.toString()));
        monitorLogRepository.save(monitorLog);
    }

}
