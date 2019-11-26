package client_service.utils;

/**
 * Created By: Prashant Chaubey
 * Created On: 22-11-2019 00:46
 * Purpose: TODO:
 **/
public final class Constants {
    private Constants() {
    }

    public class ApiV1Resource {
        private static final String PREFIX = "/api/v1";
        public static final String USER = PREFIX + "/users";
        public static final String MONITORS = PREFIX + "/monitors";
    }

    // Right now the minimum time can be configured as 5 minutes
    public static final int MIN_MONITORING_INTERVAL = 300;
}
