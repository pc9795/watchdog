package client_service.exceptions;

public class UserAlreadyExistException extends Exception {

    private static final String message = "User with username already exists: ";

    public UserAlreadyExistException(String username) {
        super(message + username);
    }

    public UserAlreadyExistException() {
    }
}
