package core.repostiories.cockroachdb;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import core.entities.cockroachdb.BaseMonitor;
import core.entities.cockroachdb.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created By: Prashant Chaubey
 * Created On: 22-11-2019 00:44
 * Purpose: Repository for Monitor resource
 **/
public interface MonitorRepository extends JpaRepository<BaseMonitor, Long> {
    BaseMonitor findById(long id);

    List<BaseMonitor> findAllByUser(User user);

    @Query("select m from BaseMonitor m where m.id>:last_id and mod(m.id,:master_count)=:master_index order by m.id asc")
    List<BaseMonitor> findWorkForMaster(Pageable pageable, @Param("last_id") long lastId,
                                        @Param("master_count") int masterCount,
                                        @Param("master_index") int masterIndex);
}
