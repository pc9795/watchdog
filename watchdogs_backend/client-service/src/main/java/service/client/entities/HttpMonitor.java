package service.client.entities;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.Positive;

/**
 * Created By: Prashant Chaubey
 * Created On: 22-11-2019 00:41
 * Purpose: TODO:
 **/
@Entity
@DiscriminatorValue("http_monitor")
public class HttpMonitor extends BaseMonitor {

    @Column()
    @Positive
    private int expectedHttpStatusCode;

    public int getExpectedHttpStatusCode() {
        return expectedHttpStatusCode;
    }

    public void setExpectedHttpStatusCode(int expectedHttpStatusCode) {
        this.expectedHttpStatusCode = expectedHttpStatusCode;
    }

}
