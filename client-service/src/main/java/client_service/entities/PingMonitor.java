package client_service.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created By: Prashant Chaubey
 * Created On: 22-11-2019 00:41
 * Purpose: TODO:
 **/
@Entity
@DiscriminatorValue("ping_monitor")
public class PingMonitor extends BaseMonitor {
}
