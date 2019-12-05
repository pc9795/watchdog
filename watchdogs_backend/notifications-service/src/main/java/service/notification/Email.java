package service.notification;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.Serializable;
import java.util.Date;

public class Email extends MimeMessage {
    private static String From;

//    public static void setUpEmailSenderAddress(String senderEmailAddress){
//        From = senderEmailAddress;
//    }

    public Email(Session session,String from, Email_Entity theEmail) throws MessagingException {
        super(session);
        this.setFrom(From);
        this.addRecipient(Message.RecipientType.TO, new InternetAddress(theEmail.getTo()));
        this.setSubject(theEmail.getSubject());
        this.setText(theEmail.getMessage());
        this.setSentDate(new Date());
        this.saveChanges();
    }

    @Override
    public String toString(){
        try {
            return ("the notification is to : " + (this.getAllRecipients().toString()));
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return  null;
    }
}
