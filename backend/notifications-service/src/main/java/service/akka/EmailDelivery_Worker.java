package service.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import service.akka.AkkaMessages.ClusterRelatedMessages.EmailConstructResponse;
import service.notification.Email;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;

public class EmailDelivery_Worker extends AbstractActor {

    private Session theSession;
    private Transport theTransport;

    static Props props(Session theSession, Transport theTransport) {
        return Props.create(EmailDelivery_Worker.class,
                () -> new EmailDelivery_Worker(theSession,theTransport));
    }

    public EmailDelivery_Worker(Session theSession, Transport theTransport) {
        this.theSession = theSession;
        this.theTransport = theTransport;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(EmailConstructResponse.class, this::GoOnDelivery)

                .build();
    }

    private void GoOnDelivery(EmailConstructResponse theEmailToSend_Container){
        Email theEmailToSend = theEmailToSend_Container.getTheEmail();

        try {
            Transport.send(theEmailToSend);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
