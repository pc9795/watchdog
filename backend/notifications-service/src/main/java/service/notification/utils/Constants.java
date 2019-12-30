package service.notification.utils;

import javax.mail.Session;
import java.util.Properties;

/**
 * Purpose: Constants for this project
 **/
public final class Constants {
    private Constants() {
    }

    public static final String configFile = "watchdog.properties";
    public static final Properties emailProperties = new Properties();
    public static final String MASTER_ACTOR_NAME = "master";
    public static final String ROUTER_ACTOR_NAME = "router";
    public static final String CLUSTER_LISTENER_ACTOR_NAME = "clusterListener";
    public static final String ACTOR_SYSTEM_NAME = "notificationActorSystem";

    //Not final as configured by properties file.
    public static String emailUsername;
    public static String emailSubject;
    public static Session emailSession;
    public static String emailFromAddr;
    public static int workers;
}
