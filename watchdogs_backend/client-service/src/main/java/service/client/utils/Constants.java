package service.client.utils;

/**
 * Created By: Prashant Chaubey
 * Created On: 22-11-2019 00:46
 * Purpose: TODO:
 **/
public final class Constants {
    private Constants() {
    }

    /**
     * Error messages for error codes.
     */
    public class ErrorMsg {
        public static final String RESOURCE_NOT_FOUND = "Resource not found";
        public static final String USER_ALREADY_EXISTS = "User with this username already exists";
        public static final String BAD_CREDENTIALS = "Bad Credentials";
        public static final String UNAUTHORIZED = "Unauthorized";
        public static final String FORBIDDEN_RESOURCE = "Forbidden Resource";
        public static final String INTERNAL_SERVER_ERROR = "Something bad happened";
    }

    public class ApiV1Resource {
        private static final String PREFIX = "/api/v1";
        public static final String USER = PREFIX + "/users";
        public static final String MONITORS = PREFIX + "/monitors";
    }

    // Right now the minimum time can be configured as 5 minutes
    public static final int MIN_MONITORING_INTERVAL = 300;
    public static final String WATCHDOG_BASE_PACKAGE = "service.client.api.v1";
}
