package service.notification;

import java.io.Serializable;

public class Email_Entity implements Serializable {

    private String to;
    private String subject;
    private String message;

    public Email_Entity(String to, String subject, String message) {
        this.to = to;
        this.subject = subject;
        this.message = message;
    }

    public String getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }
}
