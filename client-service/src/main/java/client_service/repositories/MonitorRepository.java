package client_service.repositories;

import client_service.entities.BaseMonitor;
import client_service.entities.HttpMonitor;
import client_service.entities.User;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.websocket.server.PathParam;

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

//    @Query("SELECT * from base_monitor m where m.user.id=:userId")
//    BaseMonitor getMonitorBelongingToId(@Param("userId") long userId);

//    @Bean
//    public boolean existsByUser(Long id);


//    @Bean
//    public boolean existsByUserIdAndId(@Param("user_id") long userId, @Param("id")long id);

//    @Bean
//    public boolean existsBaseMonitorByUserAndId(@PathParam("user_id") long user, @PathParam("id")long id);

}
