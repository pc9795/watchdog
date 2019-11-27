package service.client.entities;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

/**
 * Created By: Prashant Chaubey
 * Created On: 26-10-2019 01:43
 * Purpose: Roles of a user.
 **/
@Embeddable
public class UserRole {
    public enum UserRoleType {
        REGULAR, ADMIN, USER_MANAGER
    }

    @Enumerated(EnumType.STRING)
    @NotNull
    private UserRole.UserRoleType type;

    public UserRole() {
    }

    public UserRole(UserRoleType type) {
        this.type = type;
    }


    public UserRoleType getType() {
        return type;
    }

    public void setType(UserRoleType type) {
        this.type = type;
    }
}
