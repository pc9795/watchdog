package service.client.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import service.client.entities.BaseMonitor;

/**
 * Created By: Prashant Chaubey
 * Created On: 22-11-2019 00:44
 * Purpose: Repository for Monitor resource
 **/
public interface MonitorRepository extends JpaRepository<BaseMonitor, Long> {
    BaseMonitor findById(long id);

}
