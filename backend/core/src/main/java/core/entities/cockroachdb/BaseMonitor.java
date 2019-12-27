package core.entities.cockroachdb;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * Purpose: Base class for monitors
 **/
@Entity
//Using a single table strategy for storing polymorphic objects.
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.INTEGER)
//Enabling polymorphic rest resource
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = HttpMonitor.class),
        @JsonSubTypes.Type(value = PingMonitor.class),
        @JsonSubTypes.Type(value = SocketMonitor.class)
})
public class BaseMonitor {

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    @NotNull
    @Length(min = 5, max = 50)
    private String name;

    @Column(nullable = false)
    @NotNull
    private String ipOrHost;

    @Positive
    @Column(nullable = false)
    @Min(value = core.utils.Constants.MINIMUM_MONITORING_INTERVAL)
    @Max(value = core.utils.Constants.MAXIMUM_MONITORING_INTERVAL)
    private int monitoringInterval;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public BaseMonitor() {
    }

    public BaseMonitor(String name, String ipOrHost, int monitoringInterval) {
        this.name = name;
        this.ipOrHost = ipOrHost;
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

    public String getIpOrHost() {
        return ipOrHost;
    }

    public void setIpOrHost(String ipOrUrl) {
        this.ipOrHost = ipOrUrl;
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
                ", ipOrHost='" + ipOrHost + '\'' +
                ", monitoringInterval=" + monitoringInterval +
                ", user=" + user +
                '}';
    }
}
