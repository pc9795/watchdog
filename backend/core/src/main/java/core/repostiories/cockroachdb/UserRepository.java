package core.repostiories.cockroachdb;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import core.entities.cockroachdb.User;

/**
 * Created By: Prashant Chaubey
 * Created On: 22-11-2019 00:44
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
