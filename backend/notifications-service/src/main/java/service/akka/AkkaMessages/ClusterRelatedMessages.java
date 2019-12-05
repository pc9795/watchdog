package service.akka.AkkaMessages;

import service.notification.Email;

import java.io.Serializable;

// #messages
public interface ClusterRelatedMessages {

    public static final String BACKEND_REGISTRATION = "BackendRegistration";

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

    public static class EmailNotificationRequest implements Serializable {
        private final String recieverEmail;
        private final String nameOfMonitor;
        private final String timeOfDown;
        private final String monitorHttpOrHostOrIp;
        private final String httpLinkToMonitor;

        public EmailNotificationRequest(String recieverEmail, String nameOfMonitor, String timeOfDown, String monitorHttpOrHostOrIp, String httpLinkToMonitor) {
            this.recieverEmail = recieverEmail;
            this.nameOfMonitor = nameOfMonitor;
            this.timeOfDown = timeOfDown;
            this.monitorHttpOrHostOrIp = monitorHttpOrHostOrIp;
            this.httpLinkToMonitor = httpLinkToMonitor;
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

//        @Override
//        public String toString() {
//            return ("EmailRequest(" + recieverEmail + " " + nameOfMonitor + " " + timeOfDown +
//                    monitorHttpOrHostOrIp + " " + httpLinkToMonitor + ")");
//        }
    }

    public static class EmailSendRequest implements Serializable {

        private final Email theEmail;

        public EmailSendRequest(Email theEmail) {
            this.theEmail = theEmail;
        }

        public Email getTheEmail() {
            return theEmail;
        }
    }

    public static class EmailConstructResponse implements Serializable {

        private final Email theEmail;

        public EmailConstructResponse(Email theEmail) {
            this.theEmail = theEmail;
        }

        public Email getTheEmail() {
            return theEmail;
        }


    }


}