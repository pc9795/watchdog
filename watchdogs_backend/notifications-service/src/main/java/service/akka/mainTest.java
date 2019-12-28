package service.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import akka.persistence.AbstractPersistentActorWithAtLeastOnceDelivery;

import service.akka.AkkaMessages.ClusterRelatedMessages.EmailNotificationRequest;

public class mainTest {

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("emailservice");

        // create the result listener, which will print the result and shutdown the system
        final ActorRef notificationService_Supervisor = system.actorOf(
                NotificationService_Supervisor.props(1)
                , "notificationService_Supervisor");

//        Message_ToSend theMessageSent = new Message_ToSend("hello", 0);

        EmailNotificationRequest theEmailRequest = new EmailNotificationRequest(1, "ferdiafagan@gmail.com",
                "test monitor", "8", "safd", "fd");


        System.out.println("Have finnished creating, are now sending");
        // start the calculation
        notificationService_Supervisor.tell(theEmailRequest,ActorRef.noSender());
    }

}
