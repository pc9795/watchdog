package service.monitoring.utils;

import core.entities.mongodb.MonitorLog;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;

/**
 * Purpose: Utility methods for module
 **/
public final class Utils {
    private Utils() {
    }

    /**
     * Check http status for given ip or hostname
     *
     * @param ipOrHost           ip or hostname
     * @param expectedStatusCode expected http status code ex-200, 301 etc.
     * @return monitoring results
     */
    public static MonitorLog checkHttpStatus(String ipOrHost, int expectedStatusCode) {
        try {
            URL url = new URL(ipOrHost);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            //Success
            if (httpConn.getResponseCode() == expectedStatusCode) {
                return new MonitorLog(true);
            }
            //Failure
            return new MonitorLog(false, String.format("Expected %s, Found %s", expectedStatusCode,
                    httpConn.getResponseCode()));

        } catch (Exception e) {
            //Failure
            return new MonitorLog(false, e.getMessage());
        }
    }

    /**
     * Check status of a given ip or host using ping protocol
     *
     * @param ipOrHost    ip or hostname
     * @param pingTimeout timeout for the ping
     * @return monitoring log results
     */
    public static MonitorLog doPing(String ipOrHost, int pingTimeout) {
        try {
            InetAddress pingTo = InetAddress.getByName(ipOrHost);
            //Success
            if (pingTo.isReachable(pingTimeout)) {
                return new MonitorLog(true);
            }
            //Failure
            return new MonitorLog(false, "Ping not reachable");

        } catch (IOException e) {
            //Failure
            return new MonitorLog(false, e.getMessage());
        }
    }

    /**
     * Check status of a given socket address
     *
     * @param ipOrHost ip or hostname
     * @param port     socket port
     * @return monitoring log results
     */
    public static MonitorLog checkPortWorking(String ipOrHost, int port) {
        try {
            new Socket(ipOrHost, port);
            //Success
            return new MonitorLog(true);

        } catch (IOException e) {
            //Failure
            return new MonitorLog(false, e.getMessage());
        }
    }
}
