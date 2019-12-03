package service.client.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
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

    @ElementCollection
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @NotNull
    @NotEmpty
    @Valid
    private List<UserRole> roles = new ArrayList<>();

    //BaseMonitor is weak entity so enabling orphan removal
    //CascadeType.ALL so that the persistence of parent can do so for children
    //@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<BaseMonitor> monitors = new ArrayList<>();

    public User(){

    }

    public User(@NotNull @Length(min = 5, max = 20) String username, @NotNull @Length(min = 8) String password) {
        this.username = username;
        this.password = password;
    }

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
        this.monitors = (List<BaseMonitor>)monitors;
        this.monitors.forEach(monitor -> monitor.setUser(this));
    }

//    @JsonIgnore
//    public void setMonitors(List<HttpMonitor> monitors) {
//        this.monitors = monitors;
//        this.monitors.forEach(monitor -> monitor.setUser(this));
//    }
//
//    @JsonIgnore
//    public void setMonitors(List<BaseMonitor> monitors) {
//        this.monitors = monitors;
//        this.monitors.forEach(monitor -> monitor.setUser(this));
//    }
//
//    @JsonIgnore
//    public void setMonitors(List<BaseMonitor> monitors) {
//        this.monitors = monitors;
//        this.monitors.forEach(monitor -> monitor.setUser(this));
//    }


    public void addMonitor(BaseMonitor monitor) {
        this.monitors.add(monitor);
        monitor.setUser(this);
    }

    public List<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(List<UserRole> roles) {
        this.roles = roles;
    }

    /**
     * Check if user has an admin role.
     *
     * @return true if has admin role.
     */
    @JsonIgnore
    public boolean isAdmin() {
        for (UserRole role : roles) {
            if (role.getType().equals(UserRole.UserRoleType.ADMIN)) {
                return true;
            }
        }
        return false;
    }

}
