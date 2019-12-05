package service.client.entities;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.Positive;

/**
 * Created By: Prashant Chaubey
 * Created On: 22-11-2019 00:41
 * Purpose: Monitor for socket addresses
 **/
@Entity
@DiscriminatorValue("socket_monitor")
public class SocketMonitor extends BaseMonitor {

    @Column(nullable = false)
    @Positive
    private Integer socketPort;

    public int getSocketPort() {
        return socketPort;
    }

    public void setSocketPort(int socketPort) {
        this.socketPort = socketPort;
    }
}
