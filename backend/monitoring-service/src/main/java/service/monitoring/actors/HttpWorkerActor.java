package service.monitoring.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.HttpEntities;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.beans.EmailMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.monitoring.protocols.MonitoringProtocol;
import service.monitoring.utils.Constants;

/**
 * Purpose: TODO:
 **/
public class HttpWorkerActor extends AbstractActor {
    private static Logger LOGGER = LogManager.getLogger(HttpWorkerActor.class);
    private static ObjectMapper mapper = new ObjectMapper();

    public static Props props() {
        return Props.create(HttpWorkerActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(MonitoringProtocol.NotifyEmail.class,
                        obj -> notifyEmail(obj.getMessage()))
                .build();
    }

    private void notifyEmail(EmailMessage message) {
        HttpRequest request;
        try {
            request = HttpRequest.POST(Constants.notifyMessageURL).
                    withEntity(HttpEntities.create(ContentTypes.APPLICATION_JSON, mapper.writeValueAsString(message)));

        } catch (JsonProcessingException e) {
            //Currently we are just logging about delivery failures. In future we can show them as events
            LOGGER.error(String.format("Error in converting to JSON: %s", message), e);
            return;
        }
        //Right now we are executing a synchronous call we can pipe this response back to this actor to do it
        //asynchronously.
        HttpResponse response = Http.get(getContext().getSystem()).singleRequest(request).toCompletableFuture().join();
        if (response.status().isFailure()) {
            //Currently we are just logging about delivery failures. In future we can show them as events
            LOGGER.error("Delivery of email: %s failed", message);
        }
        //Stop itself
        getContext().stop(getSelf());
    }
}
