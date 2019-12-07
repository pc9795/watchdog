package core.repostiories.mongodb;

import core.entities.mongodb.MonitorLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created By: Prashant Chaubey
 * Created On: 06-12-2019 22:33
 * Purpose: Repository for monitor logs.
 **/
public interface MonitorLogRepository extends MongoRepository<MonitorLog, String> {
    MonitorLog findTopByMonitorIdAndUsernameOrderByCreationTimeDesc(long monitorId, String username);

    List<MonitorLog> findByMonitorIdAndUsernameOrderByCreationTimeDesc(Pageable pageable, long monitorId, String username);
}
