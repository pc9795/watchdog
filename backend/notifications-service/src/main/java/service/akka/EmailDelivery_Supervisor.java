package service.akka;




import akka.actor.*;
import akka.japi.Procedure;
import akka.japi.pf.DeciderBuilder;
import akka.pattern.BackoffOpts;
import akka.pattern.BackoffSupervisor;
import akka.persistence.*;
import akka.persistence.journal.japi.*;
import akka.persistence.snapshot.japi.*;

import akka.routing.RoundRobinPool;
import static akka.pattern.Patterns.ask;

import akka.japi.pf.DeciderBuilder;
import akka.actor.SupervisorStrategy;


// Email imports
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.time.Duration;
import java.util.*;

// IMPORT THE MESSAGES:
import service.akka.AkkaMessages.ClusterRelatedMessages.Confirm;
import service.akka.AkkaMessages.ClusterRelatedMessages.MsgSentSavingReciever;
import service.akka.AkkaMessages.ClusterRelatedMessages.MsgConfirmed;
import service.akka.AkkaMessages.ClusterRelatedMessages.MsgSent;

import service.akka.AkkaMessages.NotificationConstructionMessages;
import service.akka.AkkaMessages.NotificationConstructionMessages.PleaseCollectConstructersEmails_Request;
import service.akka.AkkaMessages.NotificationConstructionMessages.EmailConstructionJobOffer;
import service.akka.AkkaMessages.NotificationConstructionMessages.CollectConstructersEmails_Request;
import service.akka.AkkaMessages.NotificationConstructionMessages.CollectedConstructersEmails_Confirmation;
import service.akka.AkkaMessages.NotificationConstructionMessages.EmailToNowBeDelivered;
import service.akka.AkkaMessages.NotificationConstructionMessages.EmailToNowBeDelivered_Request;
import service.akka.AkkaMessages.NotificationConstructionMessages.EmailToNowBeDelivered_Confirmation;

public class EmailDelivery_Supervisor extends AbstractPersistentActorWithAtLeastOnceDelivery {

    private ActorRef notificationService_Supervisor;

    // constant data:
    // Email:
    // property calls
    private static final String MAIL_HOST_PROPERTY = "mail.smtp.host";
    private static final String MAIL_SOCKETFACTORY_CLASS_PROPERTY = "mail.smtp.socketFactory.class";
    private static final String MAIL_SOCKETFACTORY_FALLBACK_PROPERTY = "mail.smtp.socketFactory.fallback";
    private static final String MAIL_PORT_PROPERTY = "mail.smtp.port";
    private static final String MAIL_SOCKETFACTORY_PORT_PROPERTY = "mail.smtp.socketFactory.port";
    private static final String MAIL_AUTHARISATION_PROPERTY = "mail.smtp.auth";
    private static final String MAIL_DEBUG_PROPERTY = "mail.debug";
    private static final String MAIL_STOREPROTOCOL_PROPERTY = "mail.store.protocol";
    private static final String MAIL_TRANSPORTPROTOCOL_PROPERTY = "mail.transport.protocol";
    // there values:
    private static String EMAIL_PORT = "465";
    private static final String EMAIL_HOST = "smtp.gmail.com";
    private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
    private static final String MAIL_PROTOCOL = "pop3";
    private static final String SMTP = "smtp";

    private static String UserName = "thereallife.watchdog@gmail.com";      // TODO:DISTRIBUTE
    private static String Password = "watchdogPas";                         // TODO:DISTRIBUTE

    private Properties emailProperties;


    private Session theEmailSession;

    private int notificationCollectionId;

    private final ActorRef emailDeliveryRouter;

    private Set<Integer> constructorsCurrentlyCollectingFrom;

    private int workId_Stamp;

    private int deliverySupervisor_DeliveryIdStamp;



    static Props props(final int numberOf_Construction_Workers, final int numberOf_Delivery_Workers,
                       ActorRef notificationService_Supervisor) {
        return Props.create(EmailDelivery_Supervisor.class,
                () -> new EmailDelivery_Supervisor(numberOf_Construction_Workers, numberOf_Delivery_Workers ,
                        notificationService_Supervisor));
    }

    public EmailDelivery_Supervisor(int numberOf_Construction_Workers, int numberOf_Delivery_Workers,
                                ActorRef notificationService_Supervisor) {

        this.notificationService_Supervisor = notificationService_Supervisor;

        workId_Stamp = 0;
        deliverySupervisor_DeliveryIdStamp = 0;

        SetUpSystemPropertiesForEmail();

        for(int i = 0; i < numberOf_Construction_Workers; i++){
            ActorRef theCreatedConstructor = this.getContext().system()
                    .actorOf((EmailConstructor_Worker_Actor.props(this.getContext().getSelf(), workId_Stamp++)));
            this.getContext().watch(theCreatedConstructor);
        }

        this.emailDeliveryRouter = this.getContext().system().actorOf(new RoundRobinPool(numberOf_Delivery_Workers)
                        .props(EmailDelivery_Worker.props(emailProperties)), "emailDeliveryWorkers");

        context().watch(this.emailDeliveryRouter);

        constructorsCurrentlyCollectingFrom = new HashSet<Integer>();

        notificationCollectionId = 0;
    }

    private final void SetUpSystemPropertiesForEmail(){
        emailProperties = System.getProperties();
        emailProperties.setProperty(MAIL_HOST_PROPERTY,EMAIL_HOST);
        emailProperties.setProperty(MAIL_SOCKETFACTORY_CLASS_PROPERTY, SSL_FACTORY);
        emailProperties.setProperty(MAIL_SOCKETFACTORY_FALLBACK_PROPERTY, "false");
        emailProperties.setProperty(MAIL_PORT_PROPERTY, EMAIL_PORT);
        emailProperties.setProperty(MAIL_SOCKETFACTORY_PORT_PROPERTY, EMAIL_PORT);
        emailProperties.put(MAIL_AUTHARISATION_PROPERTY, "true");
        emailProperties.put(MAIL_DEBUG_PROPERTY, "true");
        emailProperties.put(MAIL_STOREPROTOCOL_PROPERTY, MAIL_PROTOCOL);
        emailProperties.put(MAIL_TRANSPORTPROTOCOL_PROPERTY, SMTP);
    }

    // supervision stratagy strategy
    private static SupervisorStrategy strategy =
            new OneForOneStrategy(
                    10,
                    Duration.ofMinutes(1),
                    DeciderBuilder
                            .match(ArithmeticException.class, e -> SupervisorStrategy.resume())
                            .match(NullPointerException.class, e -> SupervisorStrategy.restart())
                            .match(IllegalArgumentException.class, e -> SupervisorStrategy.stop())
                            .matchAny(o -> SupervisorStrategy.escalate())
                            .build());

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }


    @Override
    public void preStart() {
        System.out.println("Delivery worker has been succesfully created");
        System.out.println("dELIVERY supervisor path : " + this.getContext().getSelf().path());

    }

    @Override
    public String persistenceId() {
        return "Delivery-PersId";
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(PleaseCollectConstructersEmails_Request.class, this::collectPostFromConstructor)     // Recieved request from constructor to have notifications collected from it
                .match(CollectedConstructersEmails_Confirmation.class, this::confirmHaveSuccessfullyCollectedFromConstructor)    //
                .match(Confirm.class, this::confirmSentToDeliveryWorker)    //

                .build();
    }

    @Override
    public Receive createReceiveRecover() {
        return receiveBuilder().match(Object.class, evt -> updateState(evt)).build();

       // return null;        // TODO:
    }

    @Override
    public void preRestart(Throwable cause, Optional<Object> msg) {
        // do not kill all children, which is the default here
    }


    @Override
    public void postStop() throws Exception {

    }

    /**
     * update on collections from constructor workers
     * @param event
     */
    void updateState(Object event) {
        if (event instanceof MsgSentSavingReciever) {
            final MsgSentSavingReciever evt = (MsgSentSavingReciever) event;
            PleaseCollectConstructersEmails_Request requestForCollection = (PleaseCollectConstructersEmails_Request)evt.theObject;
            deliver(evt.getRecievingActor().path(), deliveryId ->
                    new CollectConstructersEmails_Request(deliveryId, requestForCollection.getRequestId()));
        } else if (event instanceof MsgConfirmed) {
            final MsgConfirmed theCollectionOfNotifications =
                    (MsgConfirmed) event;
            confirmDelivery(theCollectionOfNotifications.deliveryId);

        }
    }

    void updateStateOfDelivery(Object event){
        if (event instanceof MsgSent) {
            final MsgSent evt = (MsgSent) event;
            EmailToNowBeDelivered notificationToBeDelivered = (EmailToNowBeDelivered)evt.theObject;
            deliver(emailDeliveryRouter.path(), deliveryId ->
                    new EmailToNowBeDelivered_Request(deliveryId, notificationToBeDelivered.getTheEmail(),
                            deliverySupervisor_DeliveryIdStamp++));
        } else if (event instanceof MsgConfirmed) {
            final MsgConfirmed confirmation = (MsgConfirmed) event;
            confirmDelivery(confirmation.deliveryId);
        }
    }

//    private void passOnToDeliveryWorker(EmailConstructResponse theMsg){
//        this.emailDeliveryRouter.forward(theMsg, getContext());
//    }

    /**
     * Confirm delivery worker has recieved work
     * @param theConfirmation
     */
    private void confirmSentToDeliveryWorker(Confirm theConfirmation){
        System.out.println("confirming have delivered notification to delivery worker");
        persist(new MsgConfirmed(theConfirmation.deliveryId), evt -> updateState(evt));
    }


    /**
     * Confirm have recieved collection from constructor worker
     * @param theConfirmation
     */
    private void confirmHaveSuccessfullyCollectedFromConstructor(CollectedConstructersEmails_Confirmation theConfirmation){
        System.out.println("afdsafdsafdsDelivery supervisor is confirming have collected from constructor " + theConfirmation.deliveryId);
        persist(new MsgConfirmed(theConfirmation.deliveryId), evt -> updateState(evt));
        // Send on constructed notifications to delivery workers

        if(constructorsCurrentlyCollectingFrom.remove(theConfirmation.getConstructorWorkerId())){
            System.out.println("About to send the collection on");
            List<EmailToNowBeDelivered> notificationsToBeDelivered = theConfirmation.getTheNotificationsConsructed();
            for (EmailToNowBeDelivered notificationToBeDelivered : notificationsToBeDelivered) {
                // Send each to delivery worker
                System.out.println("email delivery : " + notificationToBeDelivered.toString());
                persist(new MsgSent(notificationToBeDelivered), evt -> updateStateOfDelivery(evt));
            }
            //notificationsToBeDelivered.addAll(theCollectionOfNotifications.getTheNotificationsConsructed());    // Add of list that is outgoing
        }

    }

    /**
     *  ask to collect from constructor worker
     * @param requestForCollection
     */
    private void collectPostFromConstructor(PleaseCollectConstructersEmails_Request requestForCollection){
        if(constructorsCurrentlyCollectingFrom.add(requestForCollection.getConstructorWorkerId())){
            System.out.println("Delivery supervisor is trying to collect from constructorsdf");
            ActorRef constructorAskingForCollecting = this.getContext().getSender();
            persist(new MsgSentSavingReciever(requestForCollection,constructorAskingForCollecting), evt -> updateState(evt));
        }
    }
}
