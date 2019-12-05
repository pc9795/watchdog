package service.notification;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;

public class Email extends MimeMessage {
    private static String From;

    public static void setUpEmailSenderAddress(String senderEmailAddress){
        From = senderEmailAddress;
    }

    public Email(Session session, String to, String subject, String message) throws MessagingException {
        super(session);
        this.setFrom(From);
        this.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        this.setSubject(subject);
        this.setText(message);
        this.setSentDate(new Date());
        this.saveChanges();
    }

    public void SetSession(Session theSession){
        this.session = theSession;          // Will be used for orphaned emails
    }
}
