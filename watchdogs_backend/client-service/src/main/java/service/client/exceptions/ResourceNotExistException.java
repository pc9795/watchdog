package service.client.exceptions;

/**
 * Created By: Prashant Chaubey
 * Created On: 26-10-2019 23:20
 * Purpose: Exception if asked resource doesn't exist in database.
 **/
public class ResourceNotExistException extends Exception {

    public ResourceNotExistException(String message) {
        super(message);
    }
}