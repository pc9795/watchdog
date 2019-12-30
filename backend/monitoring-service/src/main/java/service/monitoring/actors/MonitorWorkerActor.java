package service.monitoring.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import core.entities.cockroachdb.BaseMonitor;
import core.entities.cockroachdb.HttpMonitor;
import core.entities.cockroachdb.PingMonitor;
import core.entities.cockroachdb.SocketMonitor;
import core.entities.mongodb.MonitorLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.monitoring.protocols.MonitoringProtocol;
import service.monitoring.utils.Constants;
import service.monitoring.utils.Utils;

import java.time.Duration;

/**
 * Purpose: Child actor which will be responsible for monitoring a particular monitor
 **/
public class MonitorWorkerActor extends AbstractActor {
    private static Logger LOGGER = LoggerFactory.getLogger(MonitorWorkerActor.class.toString()); //Logger object
    private ActorRef master; //Reference to its parent
    private BaseMonitor monitor; //Monitor which it has to handle

    public MonitorWorkerActor(ActorRef master, BaseMonitor monitor) {
        this.master = master;
        this.monitor = monitor;
        LOGGER.warn(String.format("Actor created:%s", getSelf().toString()));
    }

    /**
     * Actor configuration object
     *
     * @param parent  parent actor
     * @param monitor monitor of this worker
     * @return configuration object
     */
    static Props props(ActorRef parent, BaseMonitor monitor) {
        return Props.create(MonitorWorkerActor.class, parent, monitor);
    }

    /**
     * Configuration of what behavior for what message.
     *
     * @return configuration object
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(MonitoringProtocol.StartWork.class, obj -> doWork())
                .match(MonitoringProtocol.StatusWorkerRequest.class, obj -> status(obj.getId(), obj.getReplyTo()))
                .build();
    }

    /**
     * Actual logic for monitoring by child actor
     */
    private void doWork() {
        String name = getSelf().path().name();
        LOGGER.info(String.format("%s Doing work...", name));
        MonitorLog log;
        //Http monitoring
        if (monitor instanceof HttpMonitor) {
            HttpMonitor httpMonitor = (HttpMonitor) monitor;
            log = Utils.checkHttpStatus(httpMonitor.getIpOrHost(), httpMonitor.getExpectedStatusCode(),
                    Constants.HTTP_TIMEOUT);

        } else if (monitor instanceof PingMonitor) { //Ping monitoring
            PingMonitor pingMonitor = (PingMonitor) monitor;
            log = Utils.doPing(pingMonitor.getIpOrHost(), Constants.PING_TIMEOUT);

        } else if (monitor instanceof SocketMonitor) { //Socket monitoring
            SocketMonitor socketMonitor = (SocketMonitor) monitor;
            log = Utils.checkPortWorking(socketMonitor.getIpOrHost(), socketMonitor.getSocketPort(),
                    Constants.SOCKET_TIMEOUT);

        } else {
            throw new RuntimeException(String.format("%s %s is not mapped", name, monitor.getClass().getName()));
        }
        //Update log with details to identify it
        log.setMonitorId(monitor.getId());
        log.setUsername(monitor.getUser().getUsername());

        LOGGER.info(String.format("%s Sending log:%s to monitor", name, log));
        //Send the logs to master
        master.tell(new MonitoringProtocol.UpdateLog(log, monitor), getSelf());

        //Wait for the time assigned in the monitor
        ActorSystem system = getContext().system();
        LOGGER.info(String.format("%s Waiting for: %s", name, this.monitor.getMonitoringInterval()));
        system.scheduler().scheduleOnce(Duration.ofSeconds(monitor.getMonitoringInterval()), () ->
                getSelf().tell(new MonitoringProtocol.StartWork(), getSelf()), system.dispatcher());
    }

    /**
     * Send the details of monitor currently being handled by this child actor to a sender.
     *
     * @param id      if of the monitor
     * @param replyTo sender actor
     */
    private void status(long id, ActorRef replyTo) {
        replyTo.tell(new MonitoringProtocol.StatusWorkerResponse(id, this.monitor), getSelf());
    }
}
