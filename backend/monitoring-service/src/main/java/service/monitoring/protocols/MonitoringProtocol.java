package service.monitoring.protocols;

import akka.actor.ActorRef;
import core.beans.EmailMessage;
import core.entities.cockroachdb.BaseMonitor;
import core.entities.mongodb.MonitorLog;

import java.util.List;
import java.util.Set;

/**
 * Purpose: Messaging protocol between master and actor
 **/
public class MonitoringProtocol {

    /**
     * Message for master to find work
     */
    public static class FindWork {
    }

    /**
     * Message for master to assign monitors to child actors
     */
    public static class AssignWork {
        private List<BaseMonitor> monitors;

        public AssignWork(List<BaseMonitor> monitors) {
            this.monitors = monitors;
        }

        public List<BaseMonitor> getMonitors() {
            return monitors;
        }
    }

    /**
     * Message for child actor to start work
     */
    public static class StartWork {

    }

    /**
     * Message for master to wait some time
     */
    public static class Wait {

    }

    /**
     * Message for master to update the monitoring log
     */
    public static class UpdateLog {
        private MonitorLog monitorLog;
        private BaseMonitor monitor;

        public UpdateLog(MonitorLog monitorLog, BaseMonitor monitor) {
            this.monitorLog = monitorLog;
            this.monitor = monitor;
        }

        public MonitorLog getMonitorLog() {
            return monitorLog;
        }

        public BaseMonitor getMonitor() {
            return monitor;
        }
    }

    public static class EditMonitorRequest {
        private long id;
        private BaseMonitor monitor;
        private ActorRef replyTo;

        public EditMonitorRequest(long id, BaseMonitor monitor) {
            this.id = id;
            this.monitor = monitor;
        }

        public EditMonitorRequest(long id, BaseMonitor monitor, ActorRef replyTo) {
            this.id = id;
            this.monitor = monitor;
            this.replyTo = replyTo;
        }


        public long getId() {
            return id;
        }

        public BaseMonitor getMonitor() {
            return monitor;
        }

        public ActorRef getReplyTo() {
            return replyTo;
        }
    }


    public static class EditMonitorResponse {
        private BaseMonitor monitor;

        public EditMonitorResponse(BaseMonitor monitor) {
            this.monitor = monitor;
        }

        public BaseMonitor getMonitor() {
            return monitor;
        }
    }

    public static class DeleteMonitorRequest {
        private long id;

        public DeleteMonitorRequest(long id) {
            this.id = id;
        }

        public long getId() {
            return id;
        }
    }

    public static class DeleteMonitorResponse {

    }

    public static class StatusMasterRequest {
    }

    public static class StatusMasterResponse {
        private int index;
        private Set<Long> children;

        public StatusMasterResponse(int index, Set<Long> children) {
            this.index = index;
            this.children = children;
        }

        public int getIndex() {
            return index;
        }

        public Set<Long> getChildren() {
            return children;
        }
    }

    public static class StatusWorkerRequest {
        private long id;
        private ActorRef replyTo;

        public StatusWorkerRequest(long id) {
            this.id = id;
        }

        public StatusWorkerRequest(long id, ActorRef replyTo) {
            this.id = id;
            this.replyTo = replyTo;
        }

        public long getId() {
            return id;
        }

        public ActorRef getReplyTo() {
            return replyTo;
        }
    }

    public static class StatusWorkerResponse {
        private long id;
        private BaseMonitor monitor;

        public StatusWorkerResponse(long id, BaseMonitor monitor) {
            this.id = id;
            this.monitor = monitor;
        }

        public long getId() {
            return id;
        }

        public BaseMonitor getMonitor() {
            return monitor;
        }
    }

    public static class NotifyEmail {
        private EmailMessage message;

        public NotifyEmail(EmailMessage message) {
            this.message = message;
        }

        public EmailMessage getMessage() {
            return message;
        }
    }
}
