package service.monitoring.routes;

import akka.actor.ActorRef;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.ExceptionHandler;
import akka.http.javadsl.server.Route;
import core.entities.cockroachdb.BaseMonitor;
import service.monitoring.protocols.MonitoringProtocol;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.server.PathMatchers.segment;
import static akka.pattern.Patterns.ask;
import static java.util.regex.Pattern.compile;

/**
 * Purpose: Routes for this microservice
 **/
public class MonitoringRoutes extends AllDirectives {
    private ActorRef node; //Cluster representative which will handle all requests
    private Duration askTimeout; //Time out of ask requests

    public MonitoringRoutes(ActorRef node, Duration askTimeout) {
        this.node = node;
        this.askTimeout = askTimeout;
    }

    private CompletionStage editMonitor(long id, BaseMonitor monitor) {
        return ask(node, new MonitoringProtocol.EditMonitorRequest(id, monitor), askTimeout);
    }

    private CompletionStage deleteMonitor(long id) {
        return ask(node, new MonitoringProtocol.DeleteMonitorRequest(id), askTimeout);
    }

    private CompletionStage masterStatus() {
        return ask(node, new MonitoringProtocol.StatusMasterRequest(), askTimeout);
    }

    private CompletionStage workerStatus(long id) {
        return ask(node, new MonitoringProtocol.StatusWorkerRequest(id), askTimeout);
    }

    public Route routes() {
        return handleExceptions(exceptionHandler(),
                () -> path(segment("monitoring").slash(segment("workers").slash()), () -> concat(

                        put(() -> path(segment(compile("\\d+")),
                                (idStr) -> entity(Jackson.unmarshaller(BaseMonitor.class), monitor -> {
                                    CompletionStage<?> response = editMonitor(Long.parseLong(idStr), monitor);
                                    return onSuccess(response,
                                            done -> complete(StatusCodes.OK, monitor, Jackson.marshaller()));
                                }))),

                        delete(() -> path(segment(compile("\\d+")),
                                (idStr) -> {
                                    CompletionStage<?> response = deleteMonitor(Long.parseLong(idStr));
                                    return onSuccess(response, done -> complete(StatusCodes.OK));
                                })),

                        get(() -> {
                            CompletionStage<?> response = masterStatus();
                            return onSuccess(response, done -> complete(StatusCodes.OK, done, Jackson.marshaller()));
                        }),

                        get(() -> path(segment(compile("\\d+")),
                                (idStr) -> {
                                    CompletionStage<?> response = workerStatus(Long.parseLong(idStr));
                                    return onSuccess(response,
                                            done -> complete(StatusCodes.OK, done, Jackson.marshaller()));
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
