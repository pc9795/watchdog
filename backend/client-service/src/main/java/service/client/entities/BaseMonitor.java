package service.client.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import service.client.utils.Constants;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * Created By: Prashant Chaubey
 * Created On: 22-11-2019 00:40
 * Purpose: Base class for monitors
 **/
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "monitor_type")
public class BaseMonitor {

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    @NotNull
    private String name;

    @Column(nullable = false)
    @NotNull
    private String ipOrUrlOrHost;

    @Positive
    @Column(nullable = false, columnDefinition = "int default 300")
    private int monitoringInterval;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public BaseMonitor() {
    }

    public BaseMonitor(String name, String ipOrUrlOrHost, int monitoringInterval) {
        this.name = name;
        this.ipOrUrlOrHost = ipOrUrlOrHost;
        this.monitoringInterval = monitoringInterval;
    }

    @JsonProperty
    public long getId() {
        return id;
    }

    @JsonIgnore
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIpOrUrlOrHost() {
        return ipOrUrlOrHost;
    }

    public void setIpOrUrlOrHost(String ipOrUrl) {
        this.ipOrUrlOrHost = ipOrUrl;
    }

    public int getMonitoringInterval() {
        return monitoringInterval;
    }

    public void setMonitoringInterval(int monitoringInterval) {
        this.monitoringInterval = monitoringInterval;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "BaseMonitor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", ipOrUrlOrHost='" + ipOrUrlOrHost + '\'' +
                ", monitoringInterval=" + monitoringInterval +
                ", user=" + user +
                '}';
    }
}
