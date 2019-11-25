package client_service.repositories;

import client_service.entities.BaseMonitor;
import client_service.entities.User;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created By: Prashant Chaubey
 * Created On: 22-11-2019 00:44
 * Purpose: TODO:
 **/
public interface MonitorRepository extends JpaRepository<BaseMonitor, Long> {

    @Bean
    public default BaseMonitor createMonitor(User user, BaseMonitor monitor){
        // Have checked if user with id exists from where called this
        monitor.setUser(user);

        save(monitor);

        return monitor;
    }

}
