package service.monitoring.actors;

import akka.actor.*;
import core.entities.cockroachdb.BaseMonitor;
import core.entities.cockroachdb.HttpMonitor;
import core.entities.cockroachdb.PingMonitor;
import core.entities.cockroachdb.SocketMonitor;
import core.entities.mongodb.MonitorLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.monitoring.exceptions.BadDataException;
import service.monitoring.protocols.MonitoringProtocol;
import service.monitoring.utils.Constants;
import service.monitoring.utils.Utils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.Duration;
import java.util.Set;

/**
 * Purpose: Child actor which will be responsible for monitoring a particular monitor
 **/
public class MonitorWorkerActor extends AbstractActor {
    private static Logger LOGGER = LoggerFactory.getLogger(MonitorWorkerActor.class.toString()); //Logger object
    private ActorRef master; //Reference to its parent
    private BaseMonitor monitor; //Monitor which it has to handle
    private Validator validator; //Validator to validate monitor

    public MonitorWorkerActor(ActorRef master, BaseMonitor monitor) {
        this.master = master;
        this.monitor = monitor;
        LOGGER.warn(String.format("Actor created:%s", getSelf().toString()));
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
        //Seed
        getSelf().tell(new MonitoringProtocol.StartWork(), getSelf());
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
                .match(MonitoringProtocol.EditMonitorRequest.class, obj -> editWork(obj.getId(), obj.getMonitor(),
                        obj.getReplyTo()))
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

    /**
     * Edit the monitor holded by this worker
     *
     * @param id      id of the monitor
     * @param monitor monitor information
     * @param replyTo actor to reply to
     */
    private void editWork(long id, BaseMonitor monitor, ActorRef replyTo) {
        Set<ConstraintViolation<BaseMonitor>> violations = validator.validate(monitor);
        if (violations.size() > 0) {
            ConstraintViolation<BaseMonitor> violation = violations.iterator().next();
            String property = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            LOGGER.error(String.format("First validation error:%s-%s for monitor:%s", property, message, monitor));
            replyTo.tell(new Status.Failure(new BadDataException(String.format("Validation error:%s-%s",
                    property, message))), getSelf());
            return;
        }
        LOGGER.info(String.format("Updating monitor with id:%s", id));
        //Different types
        if (!monitor.getClass().equals(this.monitor.getClass())) {
            replyTo.tell(new Status.Failure(new BadDataException(String.format("Expected:  %s, got data of type: %s",
                    this.monitor.getClass().getTypeName(), monitor.getClass().getTypeName()))), getSelf());
            return;
        }
        //Updating details
        this.monitor.setId(id);
        this.monitor.setName(monitor.getName());
        this.monitor.setIpOrHost(monitor.getIpOrHost());
        this.monitor.setMonitoringInterval(monitor.getMonitoringInterval());
        //Updating polymorphic fields
        if (monitor instanceof HttpMonitor) {
            ((HttpMonitor) this.monitor).setExpectedStatusCode(((HttpMonitor) monitor).getExpectedStatusCode());
        } else if (monitor instanceof SocketMonitor) {
            ((SocketMonitor) this.monitor).setSocketPort(((SocketMonitor) monitor).getSocketPort());
        }
        LOGGER.info(String.format("Updation of monitor with id:%s completed", id));
        replyTo.tell(new MonitoringProtocol.EditMonitorResponse(this.monitor), getSelf());
    }
}
