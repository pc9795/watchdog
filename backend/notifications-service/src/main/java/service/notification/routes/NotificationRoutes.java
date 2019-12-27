package service.notification.routes;

import akka.actor.ActorRef;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import core.beans.EmailMessage;
import service.notification.protocols.NotificationProtocol;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

import static akka.pattern.Patterns.ask;

/**
 * Created By: Prashant Chaubey
 * Created On: 27-12-2019 03:15
 * Purpose: TODO:
 **/
public class NotificationRoutes extends AllDirectives {
    private ActorRef node;
    private Duration askTimeout;

    public NotificationRoutes(ActorRef node, Duration askTimeout) {
        this.node = node;
        this.askTimeout = askTimeout;
    }

    private CompletionStage notify(EmailMessage message) {
        return ask(node, new NotificationProtocol.NotifyEmail(message), askTimeout);
    }

    public Route routes() {
        return post(
                () -> pathPrefix("notifications", () -> pathEndOrSingleSlash(
                        () -> entity(Jackson.unmarshaller(EmailMessage.class), msg -> {
                            CompletionStage<?> response = notify(msg);
                            return onSuccess(response, done ->
                                    complete(StatusCodes.OK, done, Jackson.marshaller())
                            );
                        }))
                ));
    }
}
