package service.client.exceptions;

/**
 * Purpose: Exception when data is not in expected format.
 **/
public class BadDataException extends Exception {
    public BadDataException() {
        super();
    }

    public BadDataException(String message) {
        super(message);
    }
}
