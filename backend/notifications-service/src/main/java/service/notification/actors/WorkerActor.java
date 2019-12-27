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
 * Created By: Prashant Chaubey
 * Created On: 27-12-2019 01:37
 * Purpose: TODO:
 **/
public class WorkerActor extends AbstractActor {
    private static Logger LOGGER = LogManager.getLogger(WorkerActor.class);

    public static Props props() {
        return Props.create(WorkerActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(NotificationProtocol.NotifyEmail.class, obj -> notifyViaMail(obj.getMessage(), obj.getReplyTo()))
                .build();
    }

    private void notifyViaMail(EmailMessage message, ActorRef replyTo) {
        LOGGER.info(String.format("Handled by worker:%s", getSelf().toString()));
        try {
            Utils.sendEmail(message);
            replyTo.tell(new NotificationProtocol.NotifyResponse(new NotificationResult()), getSelf());

        } catch (Exception e) {
            replyTo.tell(new NotificationProtocol.NotifyResponse(new NotificationResult(e.getMessage())), getSelf());
        }
    }
}
