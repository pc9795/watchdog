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

    User findById(long id);

    User findUserByUsername(String username);



}
