package client_service.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * Created By: Prashant Chaubey
 * Created On: 22-11-2019 00:40
 * Purpose: TODO:
 **/
@Entity
@Table(name = "base_monitors",
        uniqueConstraints ={@UniqueConstraint(columnNames = {"name","user_id"})}
)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
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

    @Column(nullable = false)
    @NotNull
    @Positive
    private int monitoringInterval;

    @JsonIgnore
    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIpOrUrl() {
        return ipOrUrlOrHost;
    }

    public void setIpOrUrl(String ipOrUrl) {
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
}
