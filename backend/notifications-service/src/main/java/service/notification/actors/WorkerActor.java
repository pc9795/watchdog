package service.notification.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Status;
import core.beans.EmailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.notification.protocols.NotificationProtocol;
import service.notification.utils.Utils;

/**
 * Purpose: The actor which implement all the primitive actions
 **/
public class WorkerActor extends AbstractActor {
    private static Logger LOGGER = LoggerFactory.getLogger(WorkerActor.class); //Logger object

    /**
     * Actor configuration object
     *
     * @return configuration object
     */
    static Props props() {
        return Props.create(WorkerActor.class);
    }

    WorkerActor() {
        LOGGER.warn(String.format("Actor created:%s", getSelf().toString()));
    }

    /**
     * Configure what action on what messages.
     *
     * @return configuration object
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(NotificationProtocol.NotifyEmail.class, obj -> notifyViaMail(obj.getMessage(), obj.getReplyTo()))
                .build();
    }

    /**
     * Send an email from given data.
     *
     * @param message email message
     * @param replyTo the actor to which it should notify of success.
     */
    private void notifyViaMail(EmailMessage message, ActorRef replyTo) {
        LOGGER.info(String.format("Handled by worker:%s", getSelf().toString()));
        try {
            Utils.sendEmail(message); //Send email

        } catch (Exception e) {
            LOGGER.error(String.format("Error in sending email:%s", message), e);
            replyTo.tell(new Status.Failure(e), getSelf());
        }
        replyTo.tell(new NotificationProtocol.NotifyResponse(), getSelf());
    }
}
