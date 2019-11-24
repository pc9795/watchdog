package client_service.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * Created By: Prashant Chaubey
 * Created On: 22-11-2019 00:41
 * Purpose: TODO:
 **/
@Entity
@Table(name = "http_monitors")
public class HttpMonitor extends BaseMonitor {

    @NotNull
    @Column(nullable = false)
    @Positive
    private int expectedHttpStatusCode;

    public int getExpectedHttpStatusCode() {
        return expectedHttpStatusCode;
    }

    public void setExpectedHttpStatusCode(int expectedHttpStatusCode) {
        this.expectedHttpStatusCode = expectedHttpStatusCode;
    }

}
