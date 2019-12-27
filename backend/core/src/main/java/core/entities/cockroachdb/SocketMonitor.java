package core.entities.cockroachdb;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.Positive;

/**
 * Purpose: Monitor for socket addresses
 **/
@Entity
@DiscriminatorValue("3")
public class SocketMonitor extends BaseMonitor {

    @Positive
    private Integer socketPort;

    public int getSocketPort() {
        return socketPort;
    }

    public void setSocketPort(int socketPort) {
        this.socketPort = socketPort;
    }
}
