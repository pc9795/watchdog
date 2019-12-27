package service.notification.protocols;

import akka.actor.ActorRef;
import core.beans.EmailMessage;
import core.beans.NotificationResult;

/**
 * Created By: Prashant Chaubey
 * Created On: 27-12-2019 01:38
 * Purpose: TODO:
 **/
public class NotificationProtocol {
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

    public static class NotifyResponse {
        private final NotificationResult result;

        public NotifyResponse(NotificationResult result) {
            this.result = result;
        }

        public NotificationResult getResult() {
            return result;
        }
    }

}
