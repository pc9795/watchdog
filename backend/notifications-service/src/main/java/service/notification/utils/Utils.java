package service.notification.utils;

import core.beans.EmailMessage;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Purpose: Utility methods for this project
 **/
public final class Utils {
    private Utils() {

    }

    /**
     * Send an email from the given details
     *
     * @param message email message
     * @throws MessagingException exception in mailing API
     */
    public static void sendEmail(EmailMessage message) throws MessagingException {
        Message messageObj = new MimeMessage(Constants.emailSession);
        messageObj.setFrom(new InternetAddress(Constants.emailFromAddr));
        messageObj.setRecipient(Message.RecipientType.TO, new InternetAddress(message.getTo()));
        messageObj.setSubject(String.format("%s - %s", Constants.emailSubject, message.getSubject()));
        messageObj.setText(message.getMessage());

        //todo uncomment
        //Transport.send(messageObj);
    }
}
