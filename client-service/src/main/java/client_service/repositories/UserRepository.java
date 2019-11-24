package client_service.repositories;

import client_service.entities.User;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created By: Prashant Chaubey
 * Created On: 22-11-2019 00:44
 * Purpose: TODO:
 **/
public interface UserRepository extends JpaRepository<User, Long> {

    @Bean
    public User findUserById(Long id);

    @Bean
    public boolean existsUserByUsername(String username);

}
