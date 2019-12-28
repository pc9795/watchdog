package core.entities.cockroachdb;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.Positive;

/**
 * Purpose: Monitoring by HTTP get
 **/
@Entity
@DiscriminatorValue("1")
public class HttpMonitor extends BaseMonitor {

    @Positive
    private Integer expectedStatusCode;

    public HttpMonitor() {
    }

    public HttpMonitor(String name, String ipOrUrlOrHost, int monitoringInterval, int expectedHttpStatusCode) {
        super(name, ipOrUrlOrHost, monitoringInterval);
        this.expectedStatusCode = expectedHttpStatusCode;
    }

    public int getExpectedStatusCode() {
        return expectedStatusCode;
    }

    public void setExpectedStatusCode(int expectedStatusCode) {
        this.expectedStatusCode = expectedStatusCode;
    }

}
