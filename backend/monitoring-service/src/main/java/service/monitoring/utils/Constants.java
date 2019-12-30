package service.monitoring.utils;

/**
 * Purpose: Constants for the project
 **/
public final class Constants {
    private Constants() {

    }

    public static final int PING_TIMEOUT = 3000;
    public static final int HTTP_TIMEOUT = 3000;
    public static final int SOCKET_TIMEOUT = 3000;
    public static final String MASTER_ACTOR_NAME = "master";
    public static final String ROUTER_ACTOR_NAME = "router";
    public static final String CLUSTER_LISTENER_ACTOR_NAME = "clusterListener";
    public static final String ACTOR_SYSTEM_NAME = "monitoringActorSystem";

    //Not final as configured by properties file.
    public static String notifyMessageURL;
}
