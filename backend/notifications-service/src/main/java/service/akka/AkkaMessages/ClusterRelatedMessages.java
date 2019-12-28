package service.akka.AkkaMessages;

import akka.actor.ActorRef;
import service.notification.Email;

import akka.serialization.*;

import java.io.Serializable;

// #messages

public interface ClusterRelatedMessages {

    public static final String BACKEND_REGISTRATION = "BackendRegistration";

//    class Msg implements Serializable {
//        private static final long serialVersionUID = 1L;
//        public final long deliveryId;
//        public final String s;
//
//        public Msg(long deliveryId,  ) {
//            this.deliveryId = deliveryId;
//            this.s = s;
//        }
//    }

    class Confirm implements Serializable {
        private static final long serialVersionUID = 1L;
        public final long deliveryId;

        public Confirm(long deliveryId) {
            this.deliveryId = deliveryId;
        }
    }

    class MsgSent implements Serializable {
        private static final long serialVersionUID = 1L;
        public final Object theObject;

        public MsgSent(Object theObject) {
            this.theObject = theObject;
        }
    }

    class MsgSentSavingReciever implements Serializable {
        private static final long serialVersionUID = 1L;
        public final Object theObject;

        private final ActorRef recievingActor;

        public MsgSentSavingReciever(Object theObject, ActorRef recievingActor) {
            this.theObject = theObject;
            this.recievingActor = recievingActor;
        }

        public ActorRef getRecievingActor() {
            return recievingActor;
        }
    }

    class MsgConfirmed implements Serializable {
        private static final long serialVersionUID = 1L;
        public final long deliveryId;

        public MsgConfirmed(long deliveryId) {
            this.deliveryId = deliveryId;
        }
    }


    /**
     * Job failed to be complete
     */
    public static class JobFailed implements Serializable {
        private final String reason;
        private final EmailNotificationRequest job;

        public JobFailed(String reason, EmailNotificationRequest job) {
            this.reason = reason;
            this.job = job;
        }

        public String getReason() {
            return reason;
        }

        public EmailNotificationRequest getJob() {
            return job;
        }

        @Override
        public String toString() {
            return "JobFailed(" + reason + ")";
        }
    }

    /**
     * Give the job to specific notification construction worker
     */
    public static class EmailNotificationRequest implements Serializable {

        private final long jobId;

        private final String recieverEmail;
        private final String nameOfMonitor;
        private final String timeOfDown;
        private final String monitorHttpOrHostOrIp;
        private final String httpLinkToMonitor;

        public EmailNotificationRequest(long jobId, String recieverEmail, String nameOfMonitor, String timeOfDown, String monitorHttpOrHostOrIp, String httpLinkToMonitor) {
            this.recieverEmail = recieverEmail;
            this.nameOfMonitor = nameOfMonitor;
            this.timeOfDown = timeOfDown;
            this.monitorHttpOrHostOrIp = monitorHttpOrHostOrIp;
            this.httpLinkToMonitor = httpLinkToMonitor;
            this.jobId = jobId;
        }

        public Long getJobId() {
            return jobId;
        }

        public String getRecieverEmail() {
            return recieverEmail;
        }

        public String getNameOfMonitor() {
            return nameOfMonitor;
        }

        public String getTimeOfDown() {
            return timeOfDown;
        }

        public String getMonitorHttpOrHostOrIp() {
            return monitorHttpOrHostOrIp;
        }

        public String getHttpLinkToMonitor() {
            return httpLinkToMonitor;
        }
    }


    /**
     * Let notification construction workers know that there is
     * a job available
     */
    public static class ConstructionWorker_JobNotice implements Serializable {

        public ConstructionWorker_JobNotice(){

        }

    }




}