package service.client.utils;

/**
 * Purpose: Constants for the project
 **/
public final class Constants {
    private Constants() {
    }

    /**
     * Error messages for error codes.
     */
    public class ErrorMsg {
        public static final String USERNAME_NOT_FOUND = "User with the username:%s is not found";
        public static final String NOT_FOUND = "Resource not found:%s";
        public static final String USER_ALREADY_EXISTS = "User with this username already exists";
        public static final String UNAUTHORIZED = "Unauthorized";
        public static final String PASSWORDS_NOT_MATCH = "Passwords not match";
        public static final String FORBIDDEN_RESOURCE = "Forbidden Resource";
        public static final String INTERNAL_SERVER_ERROR = "Something bad happened";
        public static final String BAD_REQUEST = "Bad request";
        public static final String USER_ALREADY_EXIST = "User with the username:%s already exists";
    }

    //API urls
    public class ApiV1Resource {
        private static final String PREFIX = "/api/v1";
        public static final String USER = PREFIX + "/users";
        public static final String MONITORS = PREFIX + "/monitors";
    }

    public static final int MAXIMUM_MONITORING_LOGS = 5;
    public static final String WATCHDOG_BASE_PACKAGE = "service.client.api.v1";
}
