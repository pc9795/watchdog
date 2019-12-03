package service.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.routing.RoundRobinPool;
import service.akka.AkkaMessages.ClusterRelatedMessages.EmailConstructResponse;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

public class EmailDelivery_Supervisor extends AbstractActor {

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

    private static final String EMAIL_HOST = "smtp.gmail.com";
    private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
    private static final String MAIL_PROTOCOL = "pop3";
    private static final String SMTP = "smtp";
    // end of email fixed parameters

    private final ActorRef emailDeliveryRouter;

    private Properties System_EmailProperties;
    private  final String UserName ;      // TODO:DISTRIBUTE
    private  final String Password ;                         // TODO:DISTRIBUTE
    private final String EMAIL_PORT ;

    private Session theEmailSession;

    static Props props(final int numberOfWorkers, String userName, String password, String Email_port) {
        return Props.create(EmailDelivery_Supervisor.class,
                () -> new EmailDelivery_Supervisor(numberOfWorkers,userName,  password,  Email_port));
    }

    public EmailDelivery_Supervisor(int numberOfWorkers, String userName, String password, String Email_port) {
        this.UserName = userName;
        this.Password = password;
        this.EMAIL_PORT = Email_port;
        SetUpSystemPropertiesForEmail();

        this.emailDeliveryRouter = this.getContext().actorOf(
                new RoundRobinPool(numberOfWorkers)
                        .props(Props.create(EmailConstructor_Worker_Actor.class)),
                "emailDeliveryRouter");
    }

    private final void SetUpSystemPropertiesForEmail(){
        System_EmailProperties = System.getProperties();
        System_EmailProperties.setProperty(MAIL_HOST_PROPERTY,EMAIL_HOST);
        System_EmailProperties.setProperty(MAIL_SOCKETFACTORY_CLASS_PROPERTY, SSL_FACTORY);
        System_EmailProperties.setProperty(MAIL_SOCKETFACTORY_FALLBACK_PROPERTY, "false");
        System_EmailProperties.setProperty(MAIL_PORT_PROPERTY, EMAIL_PORT);
        System_EmailProperties.setProperty(MAIL_SOCKETFACTORY_PORT_PROPERTY, EMAIL_PORT);
        System_EmailProperties.put(MAIL_AUTHARISATION_PROPERTY, "true");
        System_EmailProperties.put(MAIL_DEBUG_PROPERTY, "true");
        System_EmailProperties.put(MAIL_STOREPROTOCOL_PROPERTY, MAIL_PROTOCOL);
        System_EmailProperties.put(MAIL_TRANSPORTPROTOCOL_PROPERTY, SMTP);

        theEmailSession = Session.getDefaultInstance(System_EmailProperties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(UserName, Password);
            }
        });
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(EmailConstructResponse.class, this::passOnToDeliveryWorker)

                .build();
    }

    private void passOnToDeliveryWorker(EmailConstructResponse theMsg){
        this.emailDeliveryRouter.forward(theMsg, getContext());
    }
}
