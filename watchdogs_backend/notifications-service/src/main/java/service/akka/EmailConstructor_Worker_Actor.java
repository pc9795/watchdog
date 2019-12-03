package service.akka;

import akka.actor.ActorRef;
import service.akka.AkkaMessages.ClusterRelatedMessages.EmailConstructResponse;
import service.akka.AkkaMessages.ClusterRelatedMessages.EmailNotificationRequest;

import akka.actor.AbstractActor;
import service.notification.Email;

import javax.mail.MessagingException;
import javax.mail.Session;

// This will be incharge of making the email from the template
public class EmailConstructor_Worker_Actor extends AbstractActor {

    // Email Templpate
    private static String messageTemplate = "Hello, your service with name %s \n" +
            "(%s)\n" +
            "was reported down at : %s .\n" +
            "Please click the link below to be brought to monitor \n %s";
    private static String subjectTemplate = "Watchdogs:-Warning-%s, at downtime of %s";


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(EmailNotificationRequest.class, this::makeTheEmail)

                .build();
    }

    private void makeTheEmail(EmailNotificationRequest msg) throws MessagingException {
        Email theEmail = this.makeTheEmail(msg.getRecieverEmail(),msg.getNameOfMonitor(),msg.getTimeOfDown()
        , msg.getMonitorHttpOrHostOrIp(), msg.getHttpLinkToMonitor());

        getSender().tell(new EmailConstructResponse(theEmail), ActorRef.noSender());
    }

    private Email makeTheEmail(String to, String nameOfMonitor, String timeOfDown,
                                                String monitorHttpOrHostOrIp, String httpLinkToMonitor) throws MessagingException {


        String theSubjectTitle = String.format(subjectTemplate, nameOfMonitor, timeOfDown);
        String theMessage = String.format(messageTemplate, nameOfMonitor, monitorHttpOrHostOrIp
        , timeOfDown, httpLinkToMonitor);

        Email emailToSend = new Email(Session.getInstance(null), to, theSubjectTitle, theMessage);

        return emailToSend;
    }

//    private class Email extends MimeMessage {
//        private static String From;
//
//        public static void setUpEmailSenderAddress(String senderEmailAddress){
//            From = senderEmailAddress;
//        }
//
//        public Email(Session session, String to, String subject, String message) throws MessagingException {
//            super(session);
//            this.setFrom(From);
//            this.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
//            this.setSubject(subject);
//            this.setText(message);
//            this.setSentDate(new Date());
//        }
//
//        public void SetSession(Session theSession){
//            this.session = theSession;          // Will be used for orphaned emails
//        }
//    }
}
