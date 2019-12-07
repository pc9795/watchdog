package service.monitoring.utils;

import core.entities.mongodb.MonitorLog;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;

/**
 * Created By: Prashant Chaubey
 * Created On: 06-12-2019 00:05
 * Purpose: Utility methods for module
 **/
public final class Utils {
    private Utils() {
    }

    public static MonitorLog checkHttpStatus(String ipOrHost, int expectedStatusCode) {
        try {
            URL url = new URL(ipOrHost);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            if (httpConn.getResponseCode() == expectedStatusCode) {
                return new MonitorLog(true);
            }
            return new MonitorLog(false, String.format("Expected %s, Found %s", expectedStatusCode,
                    httpConn.getResponseCode()));

        } catch (Exception e) {
            return new MonitorLog(false, e.getMessage());
        }
    }

    public static MonitorLog doPing(String ipOrHost, int pingTimeout) {
        try {
            InetAddress pingTo = InetAddress.getByName(ipOrHost);
            if (pingTo.isReachable(pingTimeout)) {
                return new MonitorLog(true);
            }
            return new MonitorLog(false, "Ping not reachable");

        } catch (IOException e) {
            return new MonitorLog(false, e.getMessage());
        }
    }

    public static MonitorLog checkPortWorking(String ipOrHost, int port) {
        try {
            new Socket(ipOrHost, port);
            return new MonitorLog(true);

        } catch (IOException e) {
            return new MonitorLog(false, e.getMessage());
        }
    }
}
