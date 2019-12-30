package core.entities.mongodb;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

/**
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

    //For Spring data
    public MonitorLog() {
    }

    public MonitorLog(boolean status) {
        this.status = status;
        this.errorMessage = "";
        this.creationTime = LocalDateTime.now();
    }

    public MonitorLog(boolean status, String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
        this.creationTime = LocalDateTime.now();
    }

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

    public boolean isWorking() {
        return this.errorMessage.isEmpty();
    }

    @Override
    public String toString() {
        return "MonitorLog{" +
                "id='" + id + '\'' +
                ", monitorId=" + monitorId +
                ", username='" + username + '\'' +
                ", status=" + status +
                ", errorMessage='" + errorMessage + '\'' +
                ", creationTime=" + creationTime +
                '}';
    }
}
