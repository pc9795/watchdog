package service.client.exceptions;

/**
 * Purpose: If monitoring service is not working correctly
 **/
public class MonitoringServiceException extends RuntimeException {
    public MonitoringServiceException() {
        super();
    }

    public MonitoringServiceException(String message) {
        super(message);
    }
}
