package service.notification.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import core.beans.EmailMessage;
import core.beans.NotificationResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.notification.protocols.NotificationProtocol;
import service.notification.utils.Utils;

/**
 * Purpose: The actor which implement all the primitive actions
 **/
public class WorkerActor extends AbstractActor {
    private static Logger LOGGER = LogManager.getLogger(WorkerActor.class); //Logger object

    /**
     * Actor configuration object
     *
     * @return configuration object
     */
    static Props props() {
        return Props.create(WorkerActor.class);
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
            replyTo.tell(new NotificationProtocol.NotifyResponse(new NotificationResult()), getSelf()); //Success response

        } catch (Exception e) {
            //Failure response
            replyTo.tell(new NotificationProtocol.NotifyResponse(new NotificationResult(e.getMessage())), getSelf());
        }
    }
}
