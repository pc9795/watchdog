package client_service.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created By: Prashant Chaubey
 * Created On: 22-11-2019 00:40
 * Purpose: TODO:
 **/
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false, unique = true)
    @NotNull
    @Length(min = 5, max = 20)
    private String username;

    @Column(nullable = false)
    @NotNull
    @Length(min = 8)
    private String password;

    //BaseMonitor is weak entity so enabling orphan removal
    //CascadeType.ALL so that the persistence of parent can do so for children
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BaseMonitor> monitors = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }


    @JsonProperty
    public List<BaseMonitor> getMonitors() {
        return monitors;
    }

    @JsonIgnore
    public void setMonitors(List<BaseMonitor> monitors) {
        this.monitors = monitors;
        this.monitors.forEach(monitor -> monitor.setUser(this));
    }


    public void addMonitor(BaseMonitor monitor) {
        this.monitors.add(monitor);
        monitor.setUser(this);
    }

}
