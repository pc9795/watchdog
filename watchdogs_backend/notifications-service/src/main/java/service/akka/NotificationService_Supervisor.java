package service.akka;

import akka.actor.*;
import akka.cluster.pubsub.DistributedPubSubMediator;
import com.typesafe.config.ConfigFactory;

import akka.cluster.pubsub.DistributedPubSub;
//import akka.japi.Procedure;
import akka.pattern.BackoffOpts;
import akka.pattern.BackoffSupervisor;
import akka.persistence.AbstractPersistentActorWithAtLeastOnceDelivery;

import akka.persistence.*;
import akka.persistence.journal.japi.*;
import akka.persistence.snapshot.japi.*;



// import email requirements

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.*;

//import messages
import service.akka.AkkaMessages.ClusterRelatedMessages;
import service.akka.AkkaMessages.ClusterRelatedMessages.ConstructionWorker_JobNotice;
import service.akka.AkkaMessages.ClusterRelatedMessages.EmailNotificationRequest;
import service.akka.AkkaMessages.ClusterRelatedMessages.MsgSent;
import service.akka.AkkaMessages.ClusterRelatedMessages.MsgConfirmed;
import service.akka.AkkaMessages.NotificationConstructionMessages.ConstructionWorker_WorkRequest;
import service.akka.AkkaMessages.NotificationConstructionMessages.EmailConstructionJobOffer;
import service.akka.AkkaMessages.ClusterRelatedMessages.Confirm;


public class NotificationService_Supervisor extends AbstractPersistentActorWithAtLeastOnceDelivery  {

    // message caches:
//    Map<Integer, Long> jobIdMatcher = new HashMap<Integer, Long>();     // element -> (Job id for notification service, job id for the cluster)
    private Stack<EmailNotificationRequest> emailNotificcationRequest_Reserve_ToBeProscessed;
    private List<EmailNotificationRequest> emailNotificcationRequest_Reserve_BeingProcessed;


    private Stack constructionWorkers_LookingForJob = new Stack();

    // Activate Subscription Offer
    ActorRef pubsubchannel;

    // Delivery Supervisor
    ArrayList<ActorRef> deliverySupervisorPaths = new ArrayList<ActorRef>();

    static Props props(int amountOfDeliverySupervisors) {
        System.out.println("finnished making : notificationService_Supervisor props");
        return Props.create(NotificationService_Supervisor.class,
                () -> new NotificationService_Supervisor(amountOfDeliverySupervisors));
    }

    public NotificationService_Supervisor(int amountOfDeliverySupervisors){
        for(int i = 0; i < amountOfDeliverySupervisors;i++){
            deliverySupervisorPaths.add(this.getContext().system()
                    .actorOf(EmailDelivery_Supervisor.props(
                            1,1,
                            context().self()), ("deliverySupervisor" + i)));
        }

        System.out.println("finnished making : notificationService_Supervisor node");

    }

    @Override
    public String persistenceId() {
        return "persistence-id";
    }


    @Override
    public void preStart() {
        this.pubsubchannel =  DistributedPubSub.get(getContext().system()).mediator();

        System.out.println("notification service supervisor has been succesfully created");
        System.out.println("Notification service supervisor path : " + this.getContext().getSelf().path());
        emailNotificcationRequest_Reserve_ToBeProscessed = new Stack<EmailNotificationRequest>();
        emailNotificcationRequest_Reserve_BeingProcessed = new ArrayList<EmailNotificationRequest>();
    }


    @Override
    public Receive createReceive()
    {
        return receiveBuilder()
                .match(EmailNotificationRequest.class, this::publishToConstructionWorkers)
                .match(ConstructionWorker_WorkRequest.class, this::sendTheJobToConstructor)
                .match(Confirm.class, this::confirmHaveSuccessfullySentWorkToContructor)
        .build();
    }


    @Override
    public Receive createReceiveRecover() {
        System.out.println("trying to recover");
        return receiveBuilder().match(Object.class, evt -> updateState(evt)).build();
    }

    void updateState(Object event) {
        System.out.println("hello i arrived here 1");
        if (event instanceof MsgSent) {
            final MsgSent evt = (MsgSent) event;
            System.out.println("have reached here");
            ActorPath theConstructorToSendJobTo = this.getSender().path();
            System.out.println("have reached here");
            EmailNotificationRequest theNotificationRequest = (EmailNotificationRequest)evt.theObject;
            deliver(theConstructorToSendJobTo, deliveryId ->
                    new EmailConstructionJobOffer(deliveryId, theNotificationRequest.getJobId(),
                            theNotificationRequest.getRecieverEmail(), theNotificationRequest.getNameOfMonitor(),
                            theNotificationRequest.getTimeOfDown(), theNotificationRequest.getMonitorHttpOrHostOrIp(),
                            theNotificationRequest.getHttpLinkToMonitor()));
        } else if (event instanceof MsgConfirmed) {
            final MsgConfirmed evt = (MsgConfirmed) event;
            confirmDelivery(evt.deliveryId);
        }
        System.out.println("hello i left");
    }

    private void publishToConstructionWorkers(EmailNotificationRequest theEmailToSend){
        // Publish the message to NotificationConstruction_Workers
//        jobIdMatcher.put(currentJobId, theEmailToSend.getJobId());      // map job ids
//        notificationsWaitingToBeConstructed.put(currentJobId, theE)
//        currentJobId++;
        System.out.println("Advertising the job");
        emailNotificcationRequest_Reserve_ToBeProscessed.push(theEmailToSend);
        pubsubchannel.tell(new DistributedPubSubMediator.Publish("notificationConstructor_list", new ConstructionWorker_JobNotice()), getSelf());
    }

    /**
     * confirm that worker has recieved message
     * @param theConfirmation
     */
    private void confirmHaveSuccessfullySentWorkToContructor(Confirm theConfirmation){
        System.out.println("confirm constructor has recieved the job");
        persist(new MsgConfirmed(theConfirmation.deliveryId), evt -> updateState(evt));
    }

    /**
     * send Job to worker
     * @param theRequest
     */
    private void sendTheJobToConstructor(ConstructionWorker_WorkRequest theRequest){
        // TODO: add timeout
        System.out.println("sending job to the constructor");
        EmailNotificationRequest theNotificationRequest = emailNotificcationRequest_Reserve_ToBeProscessed.pop();
        persist(new MsgSent(theNotificationRequest), evt -> updateState(evt));
    }

}
