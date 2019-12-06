package service.akka;


import static service.akka.AkkaMessages.ClusterRelatedMessages.BACKEND_REGISTRATION;
import service.akka.AkkaMessages.ClusterRelatedMessages.EmailNotificationRequest;
import service.akka.AkkaMessages.ClusterRelatedMessages.JobFailed;

import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.actor.AbstractActor;

import java.util.ArrayList;
import java.util.List;

public class NotificationService_Cluster_Listener extends AbstractActor {

    List<ActorRef> backends = new ArrayList<ActorRef>();
    int jobCounter = 0;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(
                        EmailNotificationRequest.class,
                        job -> backends.isEmpty(),
                        job -> {
                            getSender()
                                    .tell(new JobFailed("Service unavailable, try again later", job), getSender());
                        })
                .match(
                        EmailNotificationRequest.class,
                        job -> {
                            jobCounter++;
                            backends.get(jobCounter % backends.size()).forward(job, getContext());
                        })
                .matchEquals(
                        BACKEND_REGISTRATION,
                        x -> {
                            getContext().watch(getSender());
                            backends.add(getSender());
                        })
                .match(
                        Terminated.class,
                        terminated -> {
                            backends.remove(terminated.getActor());
                        })
                .build();
    }

}
