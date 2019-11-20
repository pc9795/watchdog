package service;

import java.io.IOException;
import java.net.*;
import java.sql.Time;
import java.util.Timer;

public class Monitor {

    // CONSTANTS:
    private static final String HTTP_PROTOCOL = "http://";

    private static final int DEFAULT_RESPONSE_CODE = 200;




    // VARIABLES:



    // SETUP AND MAIN:

    public static void main(String[] args) {

    }




    // END OF SETUP AND MAIN;

    // MONITOR ACTIONS:

    // HTTP MONITOR
    public boolean Do_HttpMonitor(String urlOrIpAddress, int responseCode_Expected) throws IOException {
        int responseCode_Recieved = this.GetResponseCodeFromHttpConnect(urlOrIpAddress);
        if(responseCode_Recieved == responseCode_Expected){
            return true;
        }
        return false;
    }

    private int GetResponseCodeFromHttpConnect(String urlOrIpAddress) throws IOException {
        URL url = new URL(HTTP_PROTOCOL + urlOrIpAddress);
        HttpURLConnection http = (HttpURLConnection)url.openConnection();
        return http.getResponseCode();
    }
    // END OF HTTP MONITOR

    // PING MONITOR:

    public boolean Do_PingMonitor(String ipAddressOrHost) throws IOException {
        InetAddress pingTo = InetAddress.getByName(ipAddressOrHost);

        return pingTo.isReachable(5000);
    }

    // END OF PING MONITOR;

    // PORT MONITOR:

    public boolean Do_PortMonitor(String ipAddress, int portNumber) {
        try{
            Socket socket = new Socket(ipAddress,portNumber);
            return true;
        }catch (SocketTimeoutException timeOutException){
            return false;
        }catch(IOException cantConnectException){
            return false;
        }
    }



    // END OF PORT MONITOR;

    // END OF MONITOR ACTIONS;

}
