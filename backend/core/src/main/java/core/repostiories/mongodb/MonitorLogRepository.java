package core.repostiories.mongodb;

import core.entities.mongodb.MonitorLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Purpose: Repository for monitor logs.
 **/
public interface MonitorLogRepository extends MongoRepository<MonitorLog, String> {
    /**
     * Find the most recent log for given monitor id and username
     *
     * @param monitorId db id of the monitor
     * @param username  username of the user
     * @return most recent log
     */
    MonitorLog findTopByMonitorIdAndUsernameOrderByCreationTimeDesc(long monitorId, String username);

    /**
     * Find requested no of monitor logs for a given monitor id and username
     *
     * @param pageable  object containing a page number and page size so that from all the results which page should be
     *                  returned where page is obtained by dividing results into equal sets with the given size.
     * @param monitorId db id of the monitor
     * @param username  username of the user
     * @return list of monitor logs
     */
    List<MonitorLog> findByMonitorIdAndUsernameOrderByCreationTimeDesc(Pageable pageable, long monitorId, String username);
}
