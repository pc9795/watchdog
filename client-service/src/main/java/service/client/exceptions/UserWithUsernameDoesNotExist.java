package service.client.exceptions;

public class UserWithUsernameDoesNotExist extends Exception {

    private static final String message = "User with name does not exist: ";

    public UserWithUsernameDoesNotExist(String userName) {

        super(message + userName);
    }
}
