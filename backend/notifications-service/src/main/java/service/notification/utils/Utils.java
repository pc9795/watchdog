package service.notification.utils;

import core.beans.EmailMessage;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created By: Prashant Chaubey
 * Created On: 27-12-2019 00:09
 * Purpose: Utility methods for this project
 **/
public final class Utils {
    private Utils() {

    }

    public static void sendEmail(EmailMessage message) throws MessagingException {
        Message messageObj = new MimeMessage(Constants.emailSession);
        messageObj.setFrom(new InternetAddress(Constants.emailFromAddr));
        messageObj.setRecipient(Message.RecipientType.TO, new InternetAddress(message.getTo()));
        messageObj.setSubject(message.getSubject());
        messageObj.setText(message.getMessage());

        Transport.send(messageObj);
    }
}
