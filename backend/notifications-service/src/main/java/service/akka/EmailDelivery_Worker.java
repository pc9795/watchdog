package service.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.sun.mail.smtp.SMTPTransport;
import service.akka.AkkaMessages.ClusterRelatedMessages.Confirm;
import service.akka.AkkaMessages.NotificationConstructionMessages.EmailToNowBeDelivered_Request;
import service.akka.AkkaMessages.NotificationConstructionMessages;
import service.notification.Email;
import service.notification.Email_Entity;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.HashSet;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

public class EmailDelivery_Worker extends AbstractActor {

    // constant data:
    // Email:
    // property calls
    private static final String MAIL_HOST_PROPERTY = "mail.smtp.host";
    private static final String MAIL_SOCKETFACTORY_CLASS_PROPERTY = "mail.smtp.socketFactory.class";
    private static final String MAIL_SOCKETFACTORY_FALLBACK_PROPERTY = "mail.smtp.socketFactory.fallback";
    private static final String MAIL_PORT_PROPERTY = "mail.smtp.port";
    private static final String MAIL_SOCKETFACTORY_PORT_PROPERTY = "mail.smtp.socketFactory.port";
    private static final String MAIL_AUTHARISATION_PROPERTY = "mail.smtp.auth";
    private static final String MAIL_DEBUG_PROPERTY = "mail.debug";
    private static final String MAIL_STOREPROTOCOL_PROPERTY = "mail.store.protocol";
    private static final String MAIL_TRANSPORTPROTOCOL_PROPERTY = "mail.transport.protocol";
    // there values:
    private static String EMAIL_PORT = "465";
    private static final String EMAIL_HOST = "smtp.gmail.com";
    private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
    private static final String MAIL_PROTOCOL = "pop3";
    private static final String SMTP = "smtp";

    private static String UserName = "thereallife.watchdog@gmail.com";      // TODO:DISTRIBUTE
    private static String Password = "watchdogPas";                         // TODO:DISTRIBUTE

    private Properties theProperties;

    private Session theSession;
    private SMTPTransport transport;

    private Set<Integer> workersRecentDeliveryJobs;



    static Props props(Properties theProperties) {
        return Props.create(EmailDelivery_Worker.class,
                () -> new EmailDelivery_Worker(theProperties));
    }

    @Override
    public void preStart() throws Exception {
        System.out.println(self().path().name() + " is starting");
        super.preStart();
    }

    @Override
    public void postStop() {
        System.out.println("Stopped");
    }

    @Override
    public void preRestart(Throwable reason, Optional<Object> message) throws Exception {
        System.out.println(self().path().name() + " is about to restart");
        super.preRestart(reason, message);
    }

    @Override
    public void postRestart(Throwable reason) {
        System.out.println(self().path().name() + " has restarted");
    }


    public EmailDelivery_Worker(Properties theProperties) throws NoSuchProviderException {
        this.theProperties = theProperties;

        workersRecentDeliveryJobs = new HashSet<Integer>();

        // Use properties to create transport
        theSession = Session.getDefaultInstance(theProperties, new Authenticator() {
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(UserName, Password);
            }
        });

        transport = (SMTPTransport) theSession.getTransport("smtp");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(
                        Exception.class,
                        exception -> {
                            throw exception;
                        })

                .match(EmailToNowBeDelivered_Request.class, this::GoOnDelivery)

                .build();
    }

    private void GoOnDelivery(EmailToNowBeDelivered_Request theEmailToSend_Container){
//        Email theEmailToSend = theEmailToSend_Container.getTheEmail();
        System.out.println("Delivery worker has recieved job");
        this.getSender().tell(new Confirm(theEmailToSend_Container.deliveryId), getSelf());
        if(workersRecentDeliveryJobs.add(theEmailToSend_Container
                .getDeliverySupervisor_DeliveryId())){
            try {
                Email_Entity theEmail = theEmailToSend_Container.getTheEmail();


                Email theEmailToSend = new Email(theSession,UserName,theEmail);

                transport.send(theEmailToSend);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }

    }
}
