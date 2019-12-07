package core.entities.mongodb;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

/**
 * Created By: Prashant Chaubey
 * Created On: 06-12-2019 22:32
 * Purpose: Monitor logs
 **/
public class MonitorLog {

    @Id
    private String id;
    private long monitorId;
    private String username;
    private boolean status;
    private String errorMessage;
    private LocalDateTime creationTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public long getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(long monitorId) {
        this.monitorId = monitorId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
