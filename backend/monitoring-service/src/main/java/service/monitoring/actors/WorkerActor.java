package service.monitoring.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import core.entities.cockroachdb.BaseMonitor;
import core.entities.cockroachdb.HttpMonitor;
import core.entities.cockroachdb.PingMonitor;
import core.entities.cockroachdb.SocketMonitor;
import core.entities.mongodb.MonitorLog;
import service.monitoring.utils.Constants;
import service.monitoring.utils.Utils;

import java.time.Duration;
import java.util.logging.Logger;

/**
 * Created By: Prashant Chaubey
 * Created On: 06-12-2019 00:56
 * Purpose: TODO:
 **/
public class WorkerActor extends AbstractActor {
    private static Logger LOG = Logger.getLogger(WorkerActor.class.toString());
    private ActorRef master;
    private BaseMonitor monitor;

    public WorkerActor(ActorRef master, BaseMonitor monitor) {
        this.master = master;
        this.monitor = monitor;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(MonitoringProtocol.StartWork.class, obj -> {
            doWork();
        }).build();
    }

    private void doWork() {
        LOG.info(String.format("%s Doing work...", getSelf().path().name()));
        MonitorLog log;
        if (monitor instanceof HttpMonitor) {
            HttpMonitor httpMonitor = (HttpMonitor) monitor;
            log = Utils.checkHttpStatus(httpMonitor.getIpOrHost(), httpMonitor.getExpectedStatusCode());

        } else if (monitor instanceof PingMonitor) {
            PingMonitor pingMonitor = (PingMonitor) monitor;
            log = Utils.doPing(pingMonitor.getIpOrHost(), Constants.PING_TIMEOUT);

        } else if (monitor instanceof SocketMonitor) {
            SocketMonitor socketMonitor = (SocketMonitor) monitor;
            log = Utils.checkPortWorking(socketMonitor.getIpOrHost(), socketMonitor.getSocketPort());

        } else {
            //todo add custom exception
            throw new RuntimeException(String.format("%s is not mapped", monitor.getClass().getName()));
        }
        log.setMonitorId(monitor.getId());
        log.setUsername(monitor.getUser().getUsername());

        master.tell(new MonitoringProtocol.UpdateWork(log), getSelf());

        ActorSystem system = getContext().system();
        system.scheduler().scheduleOnce(Duration.ofSeconds(monitor.getMonitoringInterval()), () ->
                getSelf().tell(new MonitoringProtocol.StartWork(), getSelf()), system.dispatcher());
    }


}
