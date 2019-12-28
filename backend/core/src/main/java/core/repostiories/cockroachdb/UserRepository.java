package core.repostiories.cockroachdb;

import core.entities.cockroachdb.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Purpose: Repository for user resource
 **/
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by given id
     *
     * @param id db id of the user
     * @return user object
     */
    User findById(long id);

    /**
     * Find a user by given username
     *
     * @param username username of the user
     * @return user object
     */
    User findUserByUsername(String username);


}
