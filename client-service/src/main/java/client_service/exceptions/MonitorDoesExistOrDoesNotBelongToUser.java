package client_service.exceptions;

public class MonitorDoesExistOrDoesNotBelongToUser extends Exception {

    public MonitorDoesExistOrDoesNotBelongToUser(long userId, long monitorId) {
        super("monitor with id " + monitorId + " doesnt exist or " + " doesnt belong to user with " + userId);
    }
}
