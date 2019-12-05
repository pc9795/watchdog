package service.akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;

import akka.actor.AbstractActor;
import akka.persistence.AbstractPersistentActorWithAtLeastOnceDelivery;
import service.akka.AkkaMessages.ClusterRelatedMessages;
import service.notification.Email;

import javax.mail.MessagingException;
import javax.mail.Session;

import static akka.pattern.Patterns.ask;

// MESSAGE IMPORTS
import service.akka.AkkaMessages.ClusterRelatedMessages.EmailNotificationRequest;
import service.akka.AkkaMessages.ClusterRelatedMessages.ConstructionWorker_JobNotice;
import service.akka.AkkaMessages.ClusterRelatedMessages.Confirm;

import service.akka.AkkaMessages.NotificationConstructionMessages.ConstructionWorker_WorkRequest;
//import service.akka.AkkaMessages.NotificationConstructionMessages.RecievedConstructionJob_Confirmation;
import service.akka.AkkaMessages.NotificationConstructionMessages.CompletedConstructionJob_Confirmation;
import service.akka.AkkaMessages.NotificationConstructionMessages.EmailToNowBeDelivered;
import service.akka.AkkaMessages.NotificationConstructionMessages.EmailConstructionJobOffer;
import service.akka.AkkaMessages.NotificationConstructionMessages.PleaseCollectConstructersEmails_Request;
import service.akka.AkkaMessages.NotificationConstructionMessages.CollectConstructersEmails_Request;
import service.akka.AkkaMessages.NotificationConstructionMessages.CollectedConstructersEmails_Confirmation;
import service.notification.Email_Entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;


// This will be incharge of making the email from the template
public class EmailConstructor_Worker_Actor extends AbstractActor {

    private final int workerId;

    private ActorRef theDeliverySupervisor;

    // Email Templpate
    private static String messageTemplate = "Hello, your service with name %s \n" +
            "(%s)\n" +
            "was reported down at : %s .\n" +
            "Please click the link below to be brought to monitor \n %s";
    private static String subjectTemplate = "Watchdogs:-Warning-%s, at downtime of %s";

    private Set<Integer> thisWorkersRecievedJobs;
    private Set<Integer> thisWorkersRecentCollections;

    private Stack<EmailToNowBeDelivered> outbox;
    private Stack<EmailToNowBeDelivered> almostOutBox;

    private int currentCollection_Request_Id;

    static Props props(ActorRef theDeliverySupervisor, int workerId) {
        return Props.create(EmailConstructor_Worker_Actor.class,
                () -> new EmailConstructor_Worker_Actor(theDeliverySupervisor,workerId));
    }

    public EmailConstructor_Worker_Actor(ActorRef theDeliverySupervisor, int workerId){

        this.theDeliverySupervisor = theDeliverySupervisor;
        this.getContext().watch(this.theDeliverySupervisor);

        this.workerId = workerId;

        thisWorkersRecievedJobs = new HashSet<Integer>();
        thisWorkersRecentCollections = new HashSet<Integer>();

        outbox = new Stack<EmailToNowBeDelivered>();

        currentCollection_Request_Id = 0;
    }

    @Override
    public void preStart() throws Exception {
        ActorRef mediator = DistributedPubSub.get(getContext().system()).mediator();
        // Subscribe to topic named notificationConstructor_list
        mediator.tell(new DistributedPubSubMediator.Subscribe("notificationConstructor_list",
                getSelf()), getSelf());

        System.out.println("the path is " + this.getContext().system().name());
        System.out.println("Email Construcotr has been made");
        System.out.println("Email Construcotr path : " + this.getContext().getSelf().path());

    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(DistributedPubSubMediator.SubscribeAck.class, msg -> System.out.println("Have subscribed"))

                .match(ConstructionWorker_JobNotice.class, this::jobOffer)
                .match(EmailConstructionJobOffer.class, this::makeTheEmail)


                .match(CollectConstructersEmails_Request.class, this::collectTheEmails)

                .build();
    }

    private void jobOffer(ConstructionWorker_JobNotice jobOfferRecieved){
        System.out.println("Have recieved job offer");
        getSender().tell(new ConstructionWorker_WorkRequest(), this.getContext().getSelf());
    }

    private void makeTheEmail(EmailConstructionJobOffer msg) throws MessagingException {

        System.out.println("Constructor recieved job offer");
        // Tell sender that you have recieved
        this.getSender().tell(new Confirm(msg.deliveryId), getSelf());
        if(thisWorkersRecievedJobs.add((int)msg.deliveryId)){
            // then the deliveryId has not been processed yet
            Email_Entity theEmail = this.makeTheEmail(msg.getRecieverEmail(),msg.getNameOfMonitor(),msg.getTimeOfDown()
                    , msg.getMonitorHttpOrHostOrIp(), msg.getHttpLinkToMonitor());

            outbox.push(new EmailToNowBeDelivered(theEmail));

           this.theDeliverySupervisor.tell(
                    new PleaseCollectConstructersEmails_Request(workerId,
                            ++currentCollection_Request_Id), this.getSelf());
        }

    }

    private Email_Entity makeTheEmail(String to, String nameOfMonitor, String timeOfDown,
                                                String monitorHttpOrHostOrIp, String httpLinkToMonitor) throws MessagingException {


        String theSubjectTitle = String.format(subjectTemplate, nameOfMonitor, timeOfDown);
        String theMessage = String.format(messageTemplate, nameOfMonitor, monitorHttpOrHostOrIp
        , timeOfDown, httpLinkToMonitor);

        System.out.println("finished");

        //Email emailToSend = new Email(null, to, theSubjectTitle, theMessage);
        Email_Entity emailToSend = new Email_Entity(to, theSubjectTitle, theMessage);
        return emailToSend;
    }

    private void collectTheEmails(CollectConstructersEmails_Request theCollectionRequest){
        if(currentCollection_Request_Id == theCollectionRequest.getCollectionRequestId()){
            // Tell sender that you have recieved
            System.out.println("constructors notifications have been collected");
            almostOutBox = (Stack<EmailToNowBeDelivered>)outbox.clone();
            outbox.clear();
        }
        System.out.println("giving delivery supervisor collection");
        this.getSender().tell(new CollectedConstructersEmails_Confirmation(
                theCollectionRequest.deliveryId, almostOutBox,workerId), getSelf());
    }

//    private class Email extends MimeMessage {
//        private static String From;
//
//        public static void setUpEmailSenderAddress(String senderEmailAddress){
//            From = senderEmailAddress;
//        }
//
//        public Email(Session session, String to, String subject, String message) throws MessagingException {
//            super(session);
//            this.setFrom(From);
//            this.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
//            this.setSubject(subject);
//            this.setText(message);
//            this.setSentDate(new Date());
//        }
//
//        public void SetSession(Session theSession){
//            this.session = theSession;          // Will be used for orphaned emails
//        }
//    }
}
