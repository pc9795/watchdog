package service.client.exceptions;

/**
 * Purpose: When the user with a username already exists.
 */
public class UserAlreadyExistException extends Exception {

    public UserAlreadyExistException(String message) {
        super(message);
    }

    public UserAlreadyExistException() {
    }
}
