package service.client.exceptions;

/**
 * Purpose: Exception when a requested resource is not found.
 **/
public class ResourceNotFoundException extends Exception {

    public ResourceNotFoundException() {
        super();
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
