package service.client.repositories;

import org.springframework.stereotype.Repository;
import service.client.entities.User;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created By: Prashant Chaubey
 * Created On: 22-11-2019 00:44
 * Purpose: TODO:
 **/
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Bean
    public User findUserById(Long id);

    @Bean
    public boolean existsUserByUsername(String username);

    @Bean
    public User findUserByUsername(String username);

//    public List<User> getUsers(){
//        return userRepository.findAll();
//    }
//
//    public User createUser(User user){
//        return userRepository.save(user);
//    }

}
