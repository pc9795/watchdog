package service;

import java.io.IOException;
import java.net.*;

public class Monitor {

    // CONSTANTS
    //todo need of this constant?
    private static final String HTTP_PROTOCOL = "http://";
    private static final int PING_TIMEOUT = 5000;

    public static boolean doHTTPMonitor(String urlOrIpAddress, int expectedResponseCode) throws IOException {
        int receivedResponseCode = getResponseCodeFromHttpConn(urlOrIpAddress);
        return receivedResponseCode == expectedResponseCode;
    }

    private static int getResponseCodeFromHttpConn(String urlOrIpAddress) throws IOException {
        //todo check the possibility of adding timeout.
        URL url = new URL(HTTP_PROTOCOL + urlOrIpAddress);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        return httpConn.getResponseCode();
    }

    public static boolean doPingMonitor(String ipAddressOrHost) throws IOException {
        InetAddress pingTo = InetAddress.getByName(ipAddressOrHost);
        return pingTo.isReachable(PING_TIMEOUT);
    }

    public static boolean doPortMonitor(String ipAddress, int portNumber) {
        try {
            new Socket(ipAddress, portNumber);
            return true;
        } catch (IOException cantConnectException) {
            ///todo log these if not handling.
        }
        return false;
    }
}
