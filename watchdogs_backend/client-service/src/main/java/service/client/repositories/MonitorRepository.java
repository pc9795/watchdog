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



    List<BaseMonitor> findAll();



}
