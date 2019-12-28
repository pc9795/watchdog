package core.entities.cockroachdb;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Purpose: Monitoring by ping
 **/
@Entity
@DiscriminatorValue("2")
public class PingMonitor extends BaseMonitor {
}
