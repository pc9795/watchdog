package service;

import org.junit.Test;

import java.io.IOException;

public class MonitorTest {


    @Test
    public void monitorHttpMonitorCheckWithURLReturnsTrue() throws IOException {
        assert Monitor.doHTTPMonitor("www.google.com/", 200);
    }

    @Test
    public void monitorHttpMonitorCheckWithIPReturnsTrue() throws IOException {
        assert Monitor.doHTTPMonitor("216.58.199.164", 200);
    }

    @Test
    public void monitorPingMonitorCheckWithIPReturnsTrue() throws IOException {
        assert Monitor.doPingMonitor("127.0.0.1");
    }

    @Test
    public void monitorPingMonitorCheckWithHostReturnsTrue() throws IOException {
        assert Monitor.doPingMonitor("stackoverflow.com");
    }

    @Test
    public void monitorPortMonitorCheckWithHostReturnsTrue() {
        assert Monitor.doPortMonitor("stackoverflow.com", 80);
    }

    @Test
    public void monitorPortMonitorCheckWithIpReturnsTrue() {
        assert Monitor.doPortMonitor("127.0.0.1", 80);
    }

    @Test
    public void monitorPortMonitorCheckWithURLReturnsTrue() {
        assert Monitor.doPortMonitor("www.google.com/", 80);
    }

}