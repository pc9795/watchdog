package service.client.repositories;

import service.client.entities.BaseMonitor;
import service.client.entities.User;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created By: Prashant Chaubey
 * Created On: 22-11-2019 00:44
 * Purpose: TODO:
 **/
public interface MonitorRepository extends JpaRepository<BaseMonitor, Long>, JpaSpecificationExecutor<BaseMonitor> {

    @Bean
    public default BaseMonitor createMonitor(User user, BaseMonitor monitor){
        // Have checked if user with id exists from where called this
        monitor.setUser(user);
        BaseMonitor newMonitor = this.save(monitor);
        return newMonitor;
    }

    BaseMonitor findById(long id);

    @Bean
    boolean existsBaseMonitorById(long id);

//    @Query("SELECT * FROM base_monitors where user_id = ?0")
//    List<BaseMonitor> findBaseMonitorsByUserId(long userId);

    List<BaseMonitor> findAll();

//    @Query("SELECT * from base_monitor m where m.user.id=:userId")
//    BaseMonitor getMonitorBelongingToId(@Param("userId") long userId);

//    @Bean
//    public boolean existsByUser(Long id);


//    @Bean
//    public boolean existsByUserIdAndId(@Param("user_id") long userId, @Param("id")long id);

//    @Bean
//    public boolean existsBaseMonitorByUserAndId(@PathParam("user_id") long user, @PathParam("id")long id);

}
