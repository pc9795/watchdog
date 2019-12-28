package service.monitoring.utils;

/**
 * Purpose: Constants for the project
 **/
public final class Constants {
    private Constants() {

    }

    public static final int PING_TIMEOUT = 3000;

    //Not final as configured by properties file.
    public static String notifyMessageURL;
}
