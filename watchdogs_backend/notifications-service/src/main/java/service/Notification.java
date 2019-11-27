package service;

import service.notification.Email;

import javax.activation.*;
import javax.mail.*;
import java.util.Properties;

public class Notification {

    // Notification
    private static final String DEFAULT_HOST = "localhost";
    private static Properties System_EmailProperties;           // TODO: when distributed, this version should be kept and used, and updated


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




    private static String UserName = "thereallife.watchdog@gmail.com";
    private static String Password = "watchdogPas";




    public static void main(String[] args) {

        // TODO: change host and ports for Notification service
        /*
        if(args.length > 0){
            System.out.println("Got To here 1");
            for (int i=0; i < args.length; i++) {
                switch (args[i]) {
                    case "-h":

                        break;
                    default:
                        System.out.println("Unknown flag: " + args[i] + "\n");
                        System.out.println("Valid flags are:");
                        System.out.println("\t-h <host>\tSpecify the hostname of the target service");
                        System.out.println("\t-p <port>\tSpecify the port number of the target service");
                        System.exit(0);
                }
            }
        }else{

        }
        */
        SetUpSystemPropertiesForEmail();
        Email.setUpEmailSenderAddress(UserName);
    }

    private static final void SetUpSystemPropertiesForEmail(){
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
    }

    public Notification() {


    }

    public static void sendEmailNotification(String to, String subject, String message){
        Session session = Session.getDefaultInstance(System_EmailProperties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(UserName, Password);
            }
        });
        try {
            Email emailNotification = new Email(session, to, subject, message);
            Transport.send(emailNotification);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }



}
