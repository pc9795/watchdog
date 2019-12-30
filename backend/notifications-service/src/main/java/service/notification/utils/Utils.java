package service.notification.utils;

import core.beans.EmailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Purpose: Utility methods for this project
 **/
public final class Utils {
    private static Logger LOGGER = LoggerFactory.getLogger(Utils.class);

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
        LOGGER.info(String.format("Going to send message:%s", message));
        //todo uncomment
        //Transport.send(messageObj);
        LOGGER.info("Message sent successfully...");
    }
}
