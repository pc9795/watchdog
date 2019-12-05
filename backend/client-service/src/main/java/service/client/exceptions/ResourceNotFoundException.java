package service.client.exceptions;

/**
 * Created By: Prashant Chaubey
 * Created On: 30-11-2019 18:26
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
