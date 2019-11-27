package service.client.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created By: Prashant Chaubey
 * Created On: 22-11-2019 00:41
 * Purpose: TODO:
 **/
@Entity
@DiscriminatorValue("ping_monitor")
public class PingMonitor extends BaseMonitor {
}
