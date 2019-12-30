package service.monitoring;

import org.junit.Test;
import service.monitoring.utils.Constants;
import service.monitoring.utils.Utils;

import java.io.IOException;

public class UtilsTest {


    @Test
    public void monitorHttpMonitorCheckWithURLReturnsTrue() throws IOException {
        assert Utils.checkHttpStatus("www.google.com/", 200, Constants.HTTP_TIMEOUT).isWorking();
    }

    @Test
    public void monitorHttpMonitorCheckWithIPReturnsTrue() throws IOException {
        assert Utils.checkHttpStatus("216.58.199.164", 200, Constants.HTTP_TIMEOUT).isWorking();
    }

    @Test
    public void monitorPingMonitorCheckWithIPReturnsTrue() throws IOException {
        assert Utils.doPing("127.0.0.1", 3000).isWorking();
    }

    @Test
    public void monitorPingMonitorCheckWithHostReturnsTrue() throws IOException {
        assert Utils.doPing("stackoverflow.com", 3000).isWorking();
    }

    @Test
    public void monitorPortMonitorCheckWithHostReturnsTrue() {
        assert Utils.checkPortWorking("stackoverflow.com", 80, Constants.SOCKET_TIMEOUT).isWorking();
    }

    @Test
    public void monitorPortMonitorCheckWithIpReturnsTrue() {
        assert Utils.checkPortWorking("127.0.0.1", 80, Constants.SOCKET_TIMEOUT).isWorking();
    }

    @Test
    public void monitorPortMonitorCheckWithURLReturnsTrue() {
        assert Utils.checkPortWorking("www.google.com/", 80, Constants.SOCKET_TIMEOUT).isWorking();
    }

}