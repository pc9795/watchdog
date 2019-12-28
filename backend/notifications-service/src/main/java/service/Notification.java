//package service;
//
//import com.sun.mail.smtp.SMTPTransport;
//import service.notification.Email;
//
//import javax.activation.*;
//import javax.mail.*;
//import java.util.Properties;
//import java.util.Timer;
//import java.util.concurrent.CountDownLatch;
//
//public class Notification {
//
//    // Notification
//    private static final String DEFAULT_HOST = "localhost";
//    private void Properties System_EmailProperties;           // TODO: when distributed, this version should be kept and used, and updated
//
//
//    // constant data:
//    // Email:
//    // property calls
//    private static final String MAIL_HOST_PROPERTY = "mail.smtp.host";
//    private static final String MAIL_SOCKETFACTORY_CLASS_PROPERTY = "mail.smtp.socketFactory.class";
//    private static final String MAIL_SOCKETFACTORY_FALLBACK_PROPERTY = "mail.smtp.socketFactory.fallback";
//    private static final String MAIL_PORT_PROPERTY = "mail.smtp.port";
//    private static final String MAIL_SOCKETFACTORY_PORT_PROPERTY = "mail.smtp.socketFactory.port";
//    private static final String MAIL_AUTHARISATION_PROPERTY = "mail.smtp.auth";
//    private static final String MAIL_DEBUG_PROPERTY = "mail.debug";
//    private static final String MAIL_STOREPROTOCOL_PROPERTY = "mail.store.protocol";
//    private static final String MAIL_TRANSPORTPROTOCOL_PROPERTY = "mail.transport.protocol";
//    // there values:
//    private static String EMAIL_PORT = "465";
//    private static final String EMAIL_HOST = "smtp.gmail.com";
//    private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
//    private static final String MAIL_PROTOCOL = "pop3";
//    private static final String SMTP = "smtp";
//
//    private static String UserName = "thereallife.watchdog@gmail.com";      // TODO:DISTRIBUTE
//    private static String Password = "watchdogPas";                         // TODO:DISTRIBUTE
//
//
//
//
//
//    public static void main(String[] args) throws InterruptedException, NoSuchProviderException {
//
//        // TODO: change host and ports for Notification service
//        /*
//        if(args.length > 0){
//            System.out.println("Got To here 1");
//            for (int i=0; i < args.length; i++) {
//                switch (args[i]) {
//                    case "-h":
//
//                        break;
//                    default:
//                        System.out.println("Unknown flag: " + args[i] + "\n");
//                        System.out.println("Valid flags are:");
//                        System.out.println("\t-h <host>\tSpecify the hostname of the target service");
//                        System.out.println("\t-p <port>\tSpecify the port number of the target service");
//                        System.exit(0);
//                }
//            }
//        }else{
//
//        }
//        */
//        SetUpSystemPropertiesForEmail();
//        Email.setUpEmailSenderAddress(UserName);
//
//        theSession = Session.getDefaultInstance(System_EmailProperties, new Authenticator() {
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(UserName, Password);
//            }
//        });
//
//        transport = (SMTPTransport) theSession.getTransport("smtp");
//
//        long totalSpeed = 0;
//        for(int i = 0; i < 1 ; i++){
//            final long startTime = System.currentTimeMillis();
//            sendEmailNotification("ferdiafagan@gmail.com", "test subject", "hello, this is a test message");
//            final long afterFirst = System.currentTimeMillis();
//            sendEmailNotification("ferdiafagan@outlook.com", "test subject", "hello, this is a test message");
//            final long afterSecond = System.currentTimeMillis();
//            sendEmailNotification("ferdia.fagan@ucdconnect.ie", "test subject", "hello, this is a test message");
//            final long afterThird = System.currentTimeMillis();
//
//            totalSpeed += afterFirst - startTime;
//            System.out.println("The speed is : " + (afterFirst - startTime) + ".ms");
//            totalSpeed += afterSecond - afterFirst;
//            System.out.println("The speed is : " + (afterSecond - afterFirst) + ".ms");
//            totalSpeed += afterThird - afterSecond;
//            System.out.println("The speed is : " + (afterThird - afterSecond) + ".ms");
//        }
//
//
//        System.out.println("The average speed is : " + (totalSpeed/3) + ".ms");
//
////        long totalSpeed = 0;
////        int counter = 0;
////        int max = 3;
////        String[] t = {"ferdiafagan@gmail.com", "ferdiafagan@outlook.com", "ferdia.fagan@ucdconnect.ie"};
////
////        int numOfThread = 3;
////        CountDownLatch latch = new CountDownLatch(numOfThread);
////        for(int i = 0; i < numOfThread ; i++){
////
////            final long[] startTime = new long[numOfThread];
////            final long[] endTime = new long[numOfThread];
////            final int theCurentPos = i;
////            new Thread(new Runnable() {
////                @Override
////                public void run() {
////                    int t = 0;
////                    startTime[theCurentPos] = System.currentTimeMillis();
////                    sendEmailNotification("ferdiafagan@gmail.com", "test subject", "hello, this is a test message");
////                    endTime[theCurentPos] = System.currentTimeMillis();
////                }
////            }).start();
////
////
////
////            System.out.println("Got here after");
////
////            totalSpeed += endTime[theCurentPos] - startTime[theCurentPos];
////
////            counter++;
////            if(counter == max){
////                counter = 0;
////            }
////
//////            final long startTime = System.currentTimeMillis();
//////            sendEmailNotification("ferdiafagan@gmail.com", "test subject", "hello, this is a test message");
//////            final long afterFirst = System.currentTimeMillis();
//////            sendEmailNotification("ferdiafagan@outlook.com", "test subject", "hello, this is a test message");
//////            final long afterSecond = System.currentTimeMillis();
//////            sendEmailNotification("ferdia.fagan@ucdconnect.ie", "test subject", "hello, this is a test message");
//////            final long afterThird = System.currentTimeMillis();
//////
//////            totalSpeed += afterFirst - startTime;
//////            System.out.println("The speed is : " + (afterFirst - startTime) + ".ms");
//////            totalSpeed += afterSecond - afterFirst;
//////            System.out.println("The speed is : " + (afterSecond - afterFirst) + ".ms");
//////            totalSpeed += afterThird - afterSecond;
//////            System.out.println("The speed is : " + (afterThird - afterSecond) + ".ms");
////        }
////        latch.await();
//
//
//
//        System.out.println("The average speed is : " + (totalSpeed/30) + ".ms");
//
//
//    }
//
//    private static final void SetUpSystemPropertiesForEmail(){
//        System_EmailProperties = System.getProperties();
//        System_EmailProperties.setProperty(MAIL_HOST_PROPERTY,EMAIL_HOST);
//        System_EmailProperties.setProperty(MAIL_SOCKETFACTORY_CLASS_PROPERTY, SSL_FACTORY);
//        System_EmailProperties.setProperty(MAIL_SOCKETFACTORY_FALLBACK_PROPERTY, "false");
//        System_EmailProperties.setProperty(MAIL_PORT_PROPERTY, EMAIL_PORT);
//        System_EmailProperties.setProperty(MAIL_SOCKETFACTORY_PORT_PROPERTY, EMAIL_PORT);
//        System_EmailProperties.put(MAIL_AUTHARISATION_PROPERTY, "true");
//        System_EmailProperties.put(MAIL_DEBUG_PROPERTY, "true");
//        System_EmailProperties.put(MAIL_STOREPROTOCOL_PROPERTY, MAIL_PROTOCOL);
//        System_EmailProperties.put(MAIL_TRANSPORTPROTOCOL_PROPERTY, SMTP);
//    }
//
//    public Notification() {
//
//
//    }
//
//    public static void sendEmailNotification(String to, String subject, String message){
//        // this session will be managed better by an actor
//        // this can be distributed to
//        try {
//            Email emailNotification = new Email(theSession, to, subject, message);
//
//            System.out.println("starting send ");
//            transport.send(emailNotification);
//            System.out.println("finished send");
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//
//
//}
