package service.client.exceptions;

/**
 * Created By: Prashant Chaubey
 * Created On: 30-11-2019 22:35
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
