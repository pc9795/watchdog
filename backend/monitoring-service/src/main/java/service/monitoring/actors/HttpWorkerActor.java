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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.monitoring.protocols.MonitoringProtocol;
import service.monitoring.utils.Constants;

/**
 * Purpose: Actor responsible to communicate with notifications-service to send notifications.
 **/
public class HttpWorkerActor extends AbstractActor {
    private static Logger LOGGER = LoggerFactory.getLogger(HttpWorkerActor.class);
    private static ObjectMapper mapper = new ObjectMapper();

    static Props props() {
        return Props.create(HttpWorkerActor.class);
    }

    HttpWorkerActor() {
        LOGGER.warn(String.format("Actor created:%s", getSelf().toString()));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(MonitoringProtocol.NotifyEmail.class,
                        obj -> notifyEmail(obj.getMessage()))
                .build();
    }

    private void notifyEmail(EmailMessage message) {
        LOGGER.info(String.format("Going to send message: %s", message));
        HttpRequest request;
        try {
            request = HttpRequest.POST(Constants.notifyMessageURL).
                    withEntity(HttpEntities.create(ContentTypes.APPLICATION_JSON, mapper.writeValueAsString(message)));


            //Right now we are executing a synchronous call we can pipe this response back to this actor to do it
            //asynchronously.
            HttpResponse response = Http.get(getContext().getSystem()).singleRequest(request).
                    toCompletableFuture().join();

            if (response.status().isFailure()) {
                //Currently we are just logging about delivery failures. In future we can store them in database and
                //can show it on front-end. Or we can implement some advanced handling.
                LOGGER.error("Delivery of email: %s failed with status code: %s, and message: %s", message,
                        response.entity(), response.entity());
            } else {
                LOGGER.info(String.format("Email message is submitted successfully to notifications-service:%s", message));
            }

            //Currently we are just logging about delivery failures. In future we can store them in database and
            // can show it on front-end. Or we can implement some advanced handling.
        } catch (JsonProcessingException e) {
            LOGGER.error(String.format("Error in converting to JSON: %s", message), e);
        } catch (Exception e) {
            LOGGER.error(String.format("Error in sending message:%s", message), e);
        }
    }
}
