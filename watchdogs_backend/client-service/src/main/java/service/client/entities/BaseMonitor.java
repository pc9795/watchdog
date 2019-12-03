package service.client.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Positive;

/**
 * Created By: Prashant Chaubey
 * Created On: 22-11-2019 00:40
 * Purpose: TODO:
 **/
// uniqueConstraints ={@UniqueConstraint(columnNames = {"name","user_id"}
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "base_monitors")
@DiscriminatorColumn(name = "monitor_type")
public class BaseMonitor {

    @Id
    @GeneratedValue
    @Column
    private long id;

    @Column()
    private String name;

    @Column()
    private String ipOrUrlOrHost;

    @Column()
    @Positive
    private int monitoringInterval;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public BaseMonitor(){}

    public BaseMonitor(String name, String ipOrUrlOrHost, @Positive int monitoringInterval) {
        this.name = name;
        this.ipOrUrlOrHost = ipOrUrlOrHost;
        this.monitoringInterval = monitoringInterval;
        this.user = user;
    }

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

    public long getUserId(){
        return user.getId();
    }


    public String toString(){
        return ("id: " + id + ", name:" + name + ", ipOrUrlOrHost:" + ipOrUrlOrHost + ", monitoringInterval:" + monitoringInterval);
    }
}
