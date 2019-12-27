package core.repostiories.cockroachdb;

import core.entities.cockroachdb.BaseMonitor;
import core.entities.cockroachdb.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Purpose: Repository for Monitor resource
 **/
public interface MonitorRepository extends JpaRepository<BaseMonitor, Long> {

    /**
     * Find a monitor by its id in db.
     *
     * @param id id
     * @return monitor object
     */
    BaseMonitor findById(long id);

    /**
     * Find all monitors for a give user with give status
     *
     * @param user user object
     * @return list of monitors
     */
    List<BaseMonitor> findAllByUser(User user);

    /**
     * Find a list of  monitors which are greater than a given id which are having a particular remainder with a given
     * number. This method assumes that ids are creating in ascending order.
     *
     * @param pageable    object containing a page number and page size so that from all the results which page should be
     *                    returned where page is obtained by dividing results into equal sets with the given size.
     * @param lastId      id to which all results are greater than
     * @param masterCount no of masters
     * @param masterIndex index of the master
     * @return list of all monitors
     */
    @Query("select m from BaseMonitor m where m.id>:last_id and mod(m.id,:master_count)=:master_index order by m.id asc")
    List<BaseMonitor> findWorkForMaster(Pageable pageable, @Param("last_id") long lastId,
                                        @Param("master_count") int masterCount,
                                        @Param("master_index") int masterIndex);
}
