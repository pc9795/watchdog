package client_service.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * Created By: Prashant Chaubey
 * Created On: 22-11-2019 00:41
 * Purpose: TODO:
 **/
@Entity
@Table(name = "socket_monitors")
public class SocketMonitor extends BaseMonitor {

    @NotNull
    @Column(nullable = false)
    @Positive
    private int socketPort;

    public int getSocketPort() {
        return socketPort;
    }

    public void setSocketPort(int socketPort) {
        this.socketPort = socketPort;
    }
}
