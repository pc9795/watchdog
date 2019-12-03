package service.akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.cluster.ClusterEvent;
import akka.routing.FromConfig;
import service.akka.AkkaMessages.ClusterRelatedMessages.EmailConstructResponse;

import akka.actor.AbstractActor;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

import static akka.actor.SupervisorStrategy.escalate;
import static akka.actor.SupervisorStrategy.restart;

public class NotificationService_Supervisor extends AbstractActor {

    private ActorRef emailConstructor_Supervisor;
    private ActorRef emailDelivery_Supervisor;


    @Override
    public void preStart() {
        emailConstructor_Supervisor = getContext()
                .actorOf(FromConfig.getInstance().props(Props.create(EmailConstructor_Supervisor.class)), "emailConstruction_Supervisor");

        emailConstructor_Supervisor = getContext()
                .actorOf(FromConfig.getInstance().props(Props.create(EmailDelivery_Supervisor.class)), "emailDelivery_Supervisor");
    }

    @Override
    public Receive createReceive()
    {
        return receiveBuilder()
            .match()
        .build();
    }

    private void SendTheEmail(EmailConstructResponse theEmailToSend){

    }

}
