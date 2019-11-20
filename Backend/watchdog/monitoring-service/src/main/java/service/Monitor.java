package service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Monitor {

    // CONSTANTS:
    private static final int DEFAULT_RESPONSE_CODE = 200;


    // VARIABLES:



    // SETUP AND MAIN:

    public static void main(String[] args) {
        Monitor testMonitor = new Monitor();

        try {
            System.out.println(testMonitor.Do_HttpMonitor("http://www.google.com", DEFAULT_RESPONSE_CODE));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    // END OF SETUP AND MAIN;

    // MONITOR ACTIONS:

    private boolean Do_HttpMonitor(String urlOrIpAddress, int responseCode_Expected) throws IOException {
        URL url = new URL(urlOrIpAddress);
        HttpURLConnection http = (HttpURLConnection)url.openConnection();
        int responseCode_Recieved = http.getResponseCode();

        if(responseCode_Recieved == responseCode_Expected){
            return true;
        }
        return false;
    }



    // END OF MONITOR ACTIONS;

}
