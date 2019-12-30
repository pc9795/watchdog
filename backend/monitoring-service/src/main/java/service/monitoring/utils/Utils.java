package service.monitoring.utils;

import core.entities.mongodb.MonitorLog;

import java.io.IOException;
import java.net.*;

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
     * @param timeout            timeout for http
     * @return monitoring results
     */
    public static MonitorLog checkHttpStatus(String ipOrHost, int expectedStatusCode, int timeout) {
        try {
            URL url = new URL(ipOrHost);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setConnectTimeout(timeout);
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
     * @param ipOrHost ip or hostname
     * @param timeout  timeout for the ping
     * @return monitoring log results
     */
    public static MonitorLog doPing(String ipOrHost, int timeout) {
        try {
            InetAddress pingTo = InetAddress.getByName(ipOrHost);
            //Success
            if (pingTo.isReachable(timeout)) {
                return new MonitorLog(true);
            }
            //Failure
            return new MonitorLog(false, "Ping not reachable");

        } catch (Exception e) {
            //Failure
            return new MonitorLog(false, e.getMessage());
        }
    }

    /**
     * Check status of a given socket address
     *
     * @param ipOrHost ip or hostname
     * @param port     socket port
     * @param timeout  timeout of socket
     * @return monitoring log results
     */
    public static MonitorLog checkPortWorking(String ipOrHost, int port, int timeout) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ipOrHost, port), timeout);
            //Success
            return new MonitorLog(true);

        } catch (Exception e) {
            //Failure
            return new MonitorLog(false, e.getMessage());
        }
    }
}
