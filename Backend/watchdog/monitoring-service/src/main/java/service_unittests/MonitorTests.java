package service_unittests;

import service.Monitor;
import org.junit.Test;

import java.io.IOException;

public class MonitorTests {

    // Tests:

    // HTTP MONITOR TESTS
    @Test
    public void monitor_HttpMonitorCheck_WithURL_ReturnsTrue(){
        Monitor testMonitor = new Monitor();

        try {
            System.out.println(testMonitor.Do_HttpMonitor("www.google.com/", 200));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void monitor_HttpMonitorCheck_WithIP_ReturnsTrue(){
        Monitor testMonitor = new Monitor();

        try {
            System.out.println(testMonitor.Do_HttpMonitor("216.58.199.164", 200));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // END OF HTTP MONITOR TESTS

    // PING MONITOR TESTS:

    @Test
    public void monitor_PingMonitorCheck_WithIP_ReturnsTrue(){
        Monitor testMonitor = new Monitor();

        try {
            System.out.println(testMonitor.Do_PingMonitor("127.0.0.1"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void monitor_PingMonitorCheck_WithHost_ReturnsTrue(){
        Monitor testMonitor = new Monitor();

        try {
            System.out.println(testMonitor.Do_PingMonitor("stackoverflow.com"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // END OF PING MONITORS TESTS;

    // POST MONITOR TEST:

    @Test
    public void monitor_PortMonitorCheck_WithHost_ReturnsTrue(){
        Monitor testMonitor = new Monitor();

        System.out.println(testMonitor.Do_PortMonitor("stackoverflow.com", 80));
    }

    @Test
    public void monitor_PortMonitorCheck_WithIp_ReturnsTrue(){
        Monitor testMonitor = new Monitor();

        System.out.println(testMonitor.Do_PortMonitor("127.0.0.1", 80));
    }

    @Test
    public void monitor_PortMonitorCheck_WithURL_ReturnsTrue(){
        Monitor testMonitor = new Monitor();

        System.out.println(testMonitor.Do_PortMonitor("www.google.com/", 80));
    }


    // END OF POST MONITOR TESTS;
}
