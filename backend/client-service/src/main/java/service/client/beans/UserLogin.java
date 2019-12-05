package service.client.beans;

import javax.validation.constraints.NotNull;

/**
 * Created By: Prashant Chaubey
 * Created On: 30-11-2019 17:16
 * Purpose: Class representing the login information
 **/
public class UserLogin {
    @NotNull
    private String username;
    @NotNull
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
