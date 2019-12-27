package service.notification.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import core.beans.EmailMessage;
import service.notification.protocols.NotificationProtocol;

import java.util.ArrayDeque;

/**
 * Created By: Prashant Chaubey
 * Created On: 27-12-2019 01:37
 * Purpose: TODO:
 **/
public class MasterActor extends AbstractActor {
    private static ArrayDeque<ActorRef> workerQueue = new ArrayDeque<>();

    public MasterActor(int workers) {
        this.bootWorkers(workers);
    }

    public static Props props(int workers) {
        return Props.create(MasterActor.class, workers);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().
                match(NotificationProtocol.NotifyEmail.class, obj -> {
                    ActorRef replyTo = getSender();
                    notifyViaEmail(obj.getMessage(), replyTo);
                }).
                build();

    }

    private void bootWorkers(int workers) {
        while (workers-- > 0) {
            workerQueue.addLast(getContext().actorOf(WorkerActor.props()));
        }
    }

    private void notifyViaEmail(EmailMessage message, ActorRef replyTo) {
        workerQueue.peekFirst().tell(new NotificationProtocol.NotifyEmail(message, replyTo), getSelf());
        //Round robin
        workerQueue.addLast(workerQueue.removeFirst());
    }
}
