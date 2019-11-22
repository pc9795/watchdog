package client_service.api.v1;

import client_service.entities.User;
import client_service.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created By: Prashant Chaubey
 * Created On: 22-11-2019 00:32
 * Purpose: TODO:
 **/
@RestController("/users")
public class UserResource {

    private final UserRepository userRepository;

    @Autowired
    public UserResource(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public User addUser(@Valid User user) {
        //todo implement
        return null;
    }

    @PutMapping()
    public void updateUser(@RequestParam("user_id") long id) {
        //todo implement
    }

    @DeleteMapping
    public void deleteUser(@RequestParam("user_id") long id) {
        //todo implement
    }


}
