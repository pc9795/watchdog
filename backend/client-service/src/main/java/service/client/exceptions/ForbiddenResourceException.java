package service.client.exceptions;

/**
 * Purpose: When the user is not authorized to access a requested resource.
 */
public class ForbiddenResourceException extends Exception {

    public ForbiddenResourceException() {
        super("Forbidden resource exception");
    }
}
