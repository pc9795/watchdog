package client_service.exceptions;

public class UserAlreadyExistException extends Exception {
    public UserAlreadyExistException(String message) {
        super(message);
    }

    public UserAlreadyExistException() {
    }
}
