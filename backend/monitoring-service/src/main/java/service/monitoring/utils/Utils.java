package service.monitoring.utils;

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

    public static class MonitorResponse {
        private boolean status;
        private String errorMessage;

        public MonitorResponse(boolean status) {
            this.status = status;
            this.errorMessage = "";
        }

        public MonitorResponse(boolean status, String errorMessage) {
            this.status = status;
            this.errorMessage = errorMessage;
        }

        public boolean isFaulty() {
            return this.errorMessage.isEmpty();
        }

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    public static MonitorResponse checkHttpStatus(String ipOrHost, int expectedStatusCode) {
        try {
            URL url = new URL(ipOrHost);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            if (httpConn.getResponseCode() == expectedStatusCode) {
                return new MonitorResponse(true);
            }
            return new MonitorResponse(false, String.format("Expected %s, Found %s", expectedStatusCode,
                    httpConn.getResponseCode()));

        } catch (Exception e) {
            return new MonitorResponse(false, e.getMessage());
        }
    }

    public static MonitorResponse doPing(String ipOrHost, int pingTimeout) {
        try {
            InetAddress pingTo = InetAddress.getByName(ipOrHost);
            if (pingTo.isReachable(pingTimeout)) {
                return new MonitorResponse(true);
            }
            return new MonitorResponse(false, "Ping not reachable");

        } catch (IOException e) {
            return new MonitorResponse(false, e.getMessage());
        }
    }

    public static MonitorResponse checkPortWorking(String ipOrHost, int port) {
        try {
            new Socket(ipOrHost, port);
            return new MonitorResponse(true);

        } catch (IOException e) {
            return new MonitorResponse(false, e.getMessage());
        }
    }
}
