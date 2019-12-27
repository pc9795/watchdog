package service.notification.protocols;

import akka.actor.ActorRef;
import core.beans.EmailMessage;

/**
 * Purpose: Messages between actors in the system
 **/
public class NotificationProtocol {
    /**
     * Message to send email message and reply back to an actor about it.
     */
    public static class NotifyEmail {
        private final EmailMessage message;
        private final ActorRef replyTo;

        public NotifyEmail(EmailMessage message, ActorRef replyTo) {
            this.message = message;
            this.replyTo = replyTo;
        }

        public NotifyEmail(EmailMessage message) {
            this.message = message;
            this.replyTo = null;
        }

        public EmailMessage getMessage() {
            return message;
        }

        public ActorRef getReplyTo() {
            return replyTo;
        }
    }

    /**
     * Response of a notification action.
     */
    public static class NotifyResponse {
    }

}
