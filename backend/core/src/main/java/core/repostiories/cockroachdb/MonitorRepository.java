package core.repostiories.cockroachdb;

import org.springframework.data.jpa.repository.JpaRepository;
import core.entities.cockroachdb.BaseMonitor;
import core.entities.cockroachdb.User;

import java.util.List;

/**
 * Created By: Prashant Chaubey
 * Created On: 22-11-2019 00:44
 * Purpose: Repository for Monitor resource
 **/
public interface MonitorRepository extends JpaRepository<BaseMonitor, Long> {
    BaseMonitor findById(long id);

    List<BaseMonitor> findAllByUser(User user);
}
