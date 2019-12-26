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
 * Purpose: Child actor which will be responsible for monitoring a particular monitor
 **/
public class WorkerActor extends AbstractActor {
    private static Logger LOG = Logger.getLogger(WorkerActor.class.toString()); //Logger object
    private ActorRef master; //Reference to its parent
    private BaseMonitor monitor; //Monitor which it has to handle

    public WorkerActor(ActorRef master, BaseMonitor monitor) {
        this.master = master;
        this.monitor = monitor;
    }

    /**
     * Configuration of what behavior for what message.
     *
     * @return configuration object
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder().match(MonitoringProtocol.StartWork.class, obj -> {
            doWork();
        }).build();
    }

    /**
     * Actual logic for monitoring by child actor
     */
    private void doWork() {
        LOG.info(String.format("%s Doing work...", getSelf().path().name()));
        MonitorLog log;
        //Http monitoring
        if (monitor instanceof HttpMonitor) {
            HttpMonitor httpMonitor = (HttpMonitor) monitor;
            log = Utils.checkHttpStatus(httpMonitor.getIpOrHost(), httpMonitor.getExpectedStatusCode());

        } else if (monitor instanceof PingMonitor) { //Ping monitoring
            PingMonitor pingMonitor = (PingMonitor) monitor;
            log = Utils.doPing(pingMonitor.getIpOrHost(), Constants.PING_TIMEOUT);

        } else if (monitor instanceof SocketMonitor) { //Socket monitoring
            SocketMonitor socketMonitor = (SocketMonitor) monitor;
            log = Utils.checkPortWorking(socketMonitor.getIpOrHost(), socketMonitor.getSocketPort());

        } else {
            //todo add custom exception
            throw new RuntimeException(String.format("%s is not mapped", monitor.getClass().getName()));
        }
        //Update log with details to identify it
        log.setMonitorId(monitor.getId());
        log.setUsername(monitor.getUser().getUsername());

        //Send the logs to master
        master.tell(new MonitoringProtocol.UpdateWork(log), getSelf());

        //Wait for the time assigned in the monitor
        ActorSystem system = getContext().system();
        system.scheduler().scheduleOnce(Duration.ofSeconds(monitor.getMonitoringInterval()), () ->
                getSelf().tell(new MonitoringProtocol.StartWork(), getSelf()), system.dispatcher());
    }


}
