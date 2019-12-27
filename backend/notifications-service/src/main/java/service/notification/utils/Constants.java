package service.notification.utils;

import javax.mail.Session;
import java.util.Properties;

/**
 * Created By: Prashant Chaubey
 * Created On: 26-12-2019 23:32
 * Purpose: Constants for this project
 **/
public final class Constants {
    private Constants() {
    }

    public static final String configFile = "watchdog.properties";
    public static final Properties emailProperties = new Properties();

    //Not final as configured by properties file.
    public static String emailUsername;
    public static String emailSubject;
    public static Session emailSession;
    public static String emailFromAddr;
    public static int workers;
}
