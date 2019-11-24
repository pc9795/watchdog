package client_service.exceptions;

public class UserDoesntExist extends Exception {

    private static final String message = "User with id does not exist:";

    public UserDoesntExist(long userId) {

        super(message + userId);
    }
}
