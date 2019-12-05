package service.akka.AkkaMessages;

import service.notification.Email;
import service.notification.Email_Entity;

import java.io.Serializable;
import java.util.List;

public interface NotificationConstructionMessages {

    public static class EmailToNowBeDelivered implements Serializable {

        private final Email_Entity theEmail;

        public EmailToNowBeDelivered(Email_Entity theEmail) {
            this.theEmail = theEmail;
        }

        public Email_Entity getTheEmail() {
            return theEmail;
        }

        @Override
        public String toString(){
            return theEmail.toString();
        }
    }

    public static class EmailToNowBeDelivered_Request implements Serializable {

        private static final long serialVersionUID = 1L;
        public final long deliveryId;

        private final int deliverySupervisor_DeliveryId;

        private final Email_Entity theEmail;

        public EmailToNowBeDelivered_Request(long deliveryId,Email_Entity theEmail, int deliverySupervisor_DeliveryId) {
            this.deliveryId = deliveryId;
            this.theEmail = theEmail;
            this.deliverySupervisor_DeliveryId = deliverySupervisor_DeliveryId;
        }

        public int getDeliverySupervisor_DeliveryId() {
            return deliverySupervisor_DeliveryId;
        }

        public Email_Entity getTheEmail() {
            return theEmail;
        }
    }

    public static class EmailToNowBeDelivered_Confirmation implements Serializable {

        private static final long serialVersionUID = 1L;
        public final long deliveryId;

        public EmailToNowBeDelivered_Confirmation(long deliveryId) {
            this.deliveryId = deliveryId;
        }
    }


    public static class ConstructionWorker_WorkRequest implements Serializable{



        public ConstructionWorker_WorkRequest(){

        }

    }

//    public static class RecievedConstructionJob_Confirmation implements Serializable{
//
//        private final long jobId;
//
//        public RecievedConstructionJob_Confirmation(long jobId) {
//            this.jobId = jobId;
//        }
//    }

    public static class CompletedConstructionJob_Confirmation implements Serializable{

        private final long jobId;

        public CompletedConstructionJob_Confirmation(long jobId) {
            this.jobId = jobId;
        }
    }

    /**
     * Used for persistant sending
     */
    public static class EmailConstructionJobOffer implements Serializable {

        private static final long serialVersionUID = 1L;
        public final long deliveryId;

        private final long jobId;

        private final String recieverEmail;
        private final String nameOfMonitor;
        private final String timeOfDown;
        private final String monitorHttpOrHostOrIp;
        private final String httpLinkToMonitor;

        public EmailConstructionJobOffer(long deliveryId, long jobId, String recieverEmail, String nameOfMonitor,
                                         String timeOfDown, String monitorHttpOrHostOrIp,
                                         String httpLinkToMonitor) {
            this.recieverEmail = recieverEmail;
            this.nameOfMonitor = nameOfMonitor;
            this.timeOfDown = timeOfDown;
            this.monitorHttpOrHostOrIp = monitorHttpOrHostOrIp;
            this.httpLinkToMonitor = httpLinkToMonitor;
            this.jobId = jobId;
            this.deliveryId = deliveryId;
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


    public static class PleaseCollectConstructersEmails_Request implements Serializable{

        private final int requestId;

        private final int constructorWorkerId;  // Dont want to process collection request from same person before current collection is done

        public PleaseCollectConstructersEmails_Request(int constructorWorkerId, int requestId){
            this.requestId = requestId;
            this.constructorWorkerId = constructorWorkerId;
        }

        public int getRequestId() {
            return requestId;
        }

        public int getConstructorWorkerId() {
            return constructorWorkerId;
        }
    }

    public static class CollectConstructersEmails_Request implements Serializable{
        private static final long serialVersionUID = 1L;
        public final long deliveryId;

        private final int collectionRequestId;      // Prevent delivery processing same message twice

        public CollectConstructersEmails_Request(long deliveryId, int collectionRequestId) {
            this.deliveryId = deliveryId;
            this.collectionRequestId = collectionRequestId;
        }

        public int getCollectionRequestId() {
            return collectionRequestId;
        }
    }

    public static class CollectedConstructersEmails_Confirmation implements Serializable{
        private static final long serialVersionUID = 1L;
        public final long deliveryId;

        private final int constructorWorkerId;

        private final List<EmailToNowBeDelivered> theNotificationsConsructed;

        public CollectedConstructersEmails_Confirmation(long deliveryId,List<EmailToNowBeDelivered> theNotificationsConsructed
        ,int constructorWorkerId) {
            this.deliveryId = deliveryId;
            this.theNotificationsConsructed = theNotificationsConsructed;

            this.constructorWorkerId = constructorWorkerId;
        }

        public int getConstructorWorkerId() {
            return constructorWorkerId;
        }

        public List<EmailToNowBeDelivered> getTheNotificationsConsructed() {
            return theNotificationsConsructed;
        }
    }


}
