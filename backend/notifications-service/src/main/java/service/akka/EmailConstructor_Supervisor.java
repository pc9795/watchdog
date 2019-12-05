package service.akka;

import akka.actor.*;

import akka.routing.RoundRobinPool;
import service.akka.AkkaMessages.ClusterRelatedMessages.EmailNotificationRequest;

public class EmailConstructor_Supervisor extends AbstractActor {

    private final ActorRef emailConstructorRouter;

    private ActorRef deliveryPartner;

    static Props props(final int numberOfWorkers, ActorRef deliveryPartner) {
        return Props.create(EmailConstructor_Supervisor.class,
                () -> new EmailConstructor_Supervisor(numberOfWorkers,deliveryPartner));
    }

    public EmailConstructor_Supervisor(int numberOfWorkers, ActorRef deliveryPartner) {

        this.emailConstructorRouter = this.getContext().actorOf(
                new RoundRobinPool(numberOfWorkers)
                        .props(Props.create(EmailConstructor_Worker_Actor.class)),
                "emailConstructorWorkers");

        this.deliveryPartner = deliveryPartner;
    }

    @Override
    public void preStart() {
        // TODO: SETUP
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchEquals(
                        EmailNotificationRequest.class,
                        emailNotificationMessage -> {
                            emailConstructorRouter.tell(emailNotificationMessage, getSelf());
                        })

                .build();
    }
}