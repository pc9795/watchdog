package service.client.exceptions;

public class UsernamePasswordIncorrect extends Exception {

    private static final String message = "User with name with password does not exist: ";

    public UsernamePasswordIncorrect() {

        super(message );
    }
}