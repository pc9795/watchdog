package service.monitoring.routes;

import akka.actor.ActorRef;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCode;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.ExceptionHandler;
import akka.http.javadsl.server.Route;
import core.entities.cockroachdb.BaseMonitor;
import service.monitoring.exceptions.BadDataException;
import service.monitoring.exceptions.ResourceNotFoundException;
import service.monitoring.protocols.MonitoringProtocol;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.server.PathMatchers.segment;
import static akka.pattern.Patterns.ask;
import static java.util.regex.Pattern.compile;

/**
 * Purpose: Routes for this micro-service
 **/
public class MonitoringRoutes extends AllDirectives {
    private ActorRef node; //Cluster representative which will handle all requests
    private Duration askTimeout; //Time out of ask requests

    public MonitoringRoutes(ActorRef node, Duration askTimeout) {
        this.node = node;
        this.askTimeout = askTimeout;
    }

    /**
     * Create a new worker with updated information by deleting the old worker.
     *
     * @param id      id of the monitor
     * @param monitor monitor details
     * @return completion stage object
     */
    private CompletionStage editMonitor(long id, BaseMonitor monitor) {
        return ask(node, new MonitoringProtocol.EditMonitorRequest(id, monitor), askTimeout);
    }

    /**
     * Delete the worker which is working on the monitor of given id.
     *
     * @param id id of the monitor
     * @return completion stage object
     */
    private CompletionStage deleteMonitor(long id) {
        return ask(node, new MonitoringProtocol.DeleteMonitorRequest(id), askTimeout);
    }

    /**
     * Status of all child workers
     *
     * @return completion stage object
     */
    private CompletionStage masterStatus() {
        return ask(node, new MonitoringProtocol.StatusMasterRequest(), askTimeout);
    }

    /**
     * Status of child worker working on this monitor
     *
     * @param id id of the monitor
     * @return completion stage object
     */
    private CompletionStage workerStatus(long id) {
        return ask(node, new MonitoringProtocol.StatusWorkerRequest(id), askTimeout);
    }

    /**
     * Definition of routes.
     * Currently supported routes;
     * PUT:/monitoring/workers/{monitor_id} -> Create a new worker with updated information by deleting the old worker.
     * DELETE:/monitoring/workers/{monitor_id} -> Delete the worker which is working on the monitor of given id.
     * GET:/monitoring/workers/ -> Status of all child workers
     * GET:/monitoring/workers/{monitor_id} -> Status of child worker working on this monitor
     *
     * @return route object
     */
    public Route routes() {
        return handleExceptions(exceptionHandler(),
                () -> concat(

                        put(() -> path(segment("monitoring").slash(
                                segment("workers").slash(segment(compile("\\d+")))),
                                (idStr) -> entity(Jackson.unmarshaller(BaseMonitor.class), monitor -> {
                                    CompletionStage<?> response = editMonitor(Long.parseLong(idStr), monitor);
                                    return onSuccess(response,
                                            done -> complete(StatusCodes.OK, done, Jackson.marshaller()));
                                }))),

                        delete(() -> path(segment("monitoring").slash(
                                segment("workers").slash(segment(compile("\\d+")))),
                                (idStr) -> {
                                    CompletionStage<?> response = deleteMonitor(Long.parseLong(idStr));
                                    return onSuccess(response, done -> complete(StatusCodes.OK));
                                })),

                        get(() -> path(segment("monitoring").slash(segment("workers").slash()), () -> {
                            CompletionStage<?> response = masterStatus();
                            return onSuccess(response, done -> complete(StatusCodes.OK, done, Jackson.marshaller()));
                        })),

                        get(() -> path(segment("monitoring").slash(
                                segment("workers").slash(segment(compile("\\d+")))),
                                (idStr) -> {
                                    CompletionStage<?> response = workerStatus(Long.parseLong(idStr));
                                    return onSuccess(response,
                                            done -> complete(StatusCodes.OK, done, Jackson.marshaller()));
                                }))
                ));
    }

    /**
     * Exception handler for the routes
     *
     * @return exception handler object
     */
    private ExceptionHandler exceptionHandler() {
        return ExceptionHandler.newBuilder()
                .match(BadDataException.class, obj -> complete(StatusCodes.BAD_REQUEST, obj.getMessage()))
                .match(ResourceNotFoundException.class, obj -> complete(StatusCodes.NOT_FOUND, obj.getMessage()))
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
