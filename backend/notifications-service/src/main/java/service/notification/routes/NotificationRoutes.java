package service.notification.routes;

import akka.actor.ActorRef;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.ExceptionHandler;
import akka.http.javadsl.server.Route;
import core.beans.EmailMessage;
import service.notification.protocols.NotificationProtocol;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

import static akka.pattern.Patterns.ask;

/**
 * Purpose: Routes for this microservice
 **/
public class NotificationRoutes extends AllDirectives {
    private ActorRef node; //Cluster representative which will handle all requests
    private Duration askTimeout; //Time out of ask requests

    public NotificationRoutes(ActorRef node, Duration askTimeout) {
        this.node = node;
        this.askTimeout = askTimeout;
    }

    /**
     * Notify using a email with given message
     *
     * @param message email message
     * @return completion stage object
     */
    private CompletionStage notify(EmailMessage message) {
        return ask(node, new NotificationProtocol.NotifyEmail(message), askTimeout);
    }

    /**
     * Definition of routes.
     * Currently supported routes;
     * POST:/notifications/ -> Send an EmailMessage for email notification
     *
     * @return route object
     */
    public Route routes() {
        return handleExceptions(exceptionHandler(), () -> post(
                () -> pathPrefix("notifications", () -> pathEndOrSingleSlash(
                        () -> entity(Jackson.unmarshaller(EmailMessage.class), msg -> {
                            CompletionStage<?> response = notify(msg);
                            return onSuccess(response, done ->
                                    complete(StatusCodes.CREATED)
                            );
                        }))
                )));
    }

    /**
     * Exception handler for the routes
     *
     * @return exception handler object
     */
    private ExceptionHandler exceptionHandler() {
        return ExceptionHandler.newBuilder()
                .match(Exception.class, obj -> {
                    if (obj != null) {
                        return complete(StatusCodes.INTERNAL_SERVER_ERROR,
                                String.format("Something Bad Happened: %s - %s", obj.getClass(), obj.getMessage()));
                    }
                    return complete(StatusCodes.INTERNAL_SERVER_ERROR, "Something Bad Happened!");
                })
                .build();
    }
}
