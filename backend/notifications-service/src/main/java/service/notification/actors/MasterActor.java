package service.notification.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import core.beans.EmailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.notification.protocols.NotificationProtocol;

import java.util.ArrayDeque;

/**
 * Purpose: Master actor which will maintain a list of workers to assign tasks.
 **/
public class MasterActor extends AbstractActor {
    private static Logger LOGGER = LoggerFactory.getLogger(MasterActor.class);
    private static ArrayDeque<ActorRef> workerQueue = new ArrayDeque<>(); //Queue of workers

    public MasterActor(int workers) {
        LOGGER.warn(String.format("Actor created:%s", getSelf().toString()));
        this.bootWorkers(workers);
    }

    /**
     * Actor configuration object
     *
     * @param workers no of workers to manage
     * @return configuration object
     */
    static Props props(int workers) {
        return Props.create(MasterActor.class, workers);
    }

    /**
     * Configure what action on what messages.
     *
     * @return configuration objectF
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder().
                match(NotificationProtocol.NotifyEmail.class, obj -> {
                    ActorRef replyTo = getSender();
                    notifyViaEmail(obj.getMessage(), replyTo);
                }).
                build();

    }

    /**
     * Create a queue of workers.
     *
     * @param workers no of workers
     */
    private void bootWorkers(int workers) {
        LOGGER.info(String.format("Master actor is booting %s workers", workers));
        while (workers-- > 0) {
            workerQueue.addLast(getContext().actorOf(WorkerActor.props()));
        }
    }

    /**
     * Select a worker from the queue and assign them the email message to process
     *
     * @param message email message
     * @param replyTo worker should directly apply to the assigner of the work(wo gave it to master)
     */
    private void notifyViaEmail(EmailMessage message, ActorRef replyTo) {
        LOGGER.info(String.format("Master actor assigning an email to child: %s", message));
        workerQueue.peekFirst().tell(new NotificationProtocol.NotifyEmail(message, replyTo), getSelf());
        //Round robin
        workerQueue.addLast(workerQueue.removeFirst());
    }
}
