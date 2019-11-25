package client_service.api.v1;

import client_service.entities.BaseMonitor;
import client_service.entities.HttpMonitor;
import client_service.entities.User;
import client_service.exceptions.ResourceNotExistException;
import client_service.exceptions.UserAlreadyExistException;
import client_service.exceptions.UserDoesntExist;
import client_service.repositories.MonitorRepository;
import client_service.repositories.UserRepository;
import client_service.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.util.List;

/**
 * Created By: Prashant Chaubey
 * Created On: 22-11-2019 00:32
 * Purpose: TODO:
 **/
@RestController
@RequestMapping(Constants.ApiV1Resource.USER)
public class UserResource {

    private final UserRepository userRepository;
    private final MonitorRepository monitorRepository;

    @Autowired
    public UserResource(MonitorRepository monitorRepository, UserRepository userRepository) {
        this.monitorRepository = monitorRepository;
        this.userRepository = userRepository;

        this.userRepository.save(new User("ferdia", "passssssss"));
        this.userRepository.save(new User("dummy", "passssssss"));
    }

    //@RequestMapping(value="/applications",method= RequestMethod.POST)
    @PostMapping
    public User addUser(@PathParam("user") @Valid User user) throws UserAlreadyExistException {
        //todo implement
        throwException_IfUsernameAlreadyExist(user.getUsername());
        // Create user
        this.userRepository.save(user);

        return user;
    }

    @PostMapping("/{user_id}/addMonitor/")
    public HttpMonitor addMonitorToUser(@PathVariable("user_id") long userId, @PathParam("http_monitor") @Valid HttpMonitor httpMonitor) throws UserDoesntExist {
        //todo implement
        System.out.println("Got here");
        if(!userRepository.existsById(userId)){
            throw new UserDoesntExist(userId);
        }
        System.out.println("Got here 1");
        // Then no error
        User userWithId = userRepository.findUserById(userId);
        System.out.println("Got here 2");
        BaseMonitor theNewMonitor = monitorRepository.createMonitor(userWithId,httpMonitor);
        System.out.println("Got here 3");
        userWithId.addMonitor(theNewMonitor);
        System.out.println("Got here 4");
        return (HttpMonitor) theNewMonitor;
    }

    //("/{user_id}")
    @GetMapping
    public User getUser(@PathParam("user_id") long id) throws UserDoesntExist {
        //todo implement
        throwException_IfUserIdDoesntExist(id);

        return this.userRepository.findUserById(id);
    }

    @PutMapping()
    public void updateUser(@PathParam("user_id") long id, @PathParam("user") User user) throws UserDoesntExist, UserAlreadyExistException {
        //todo implement
        throwException_IfUserIdDoesntExist(id);
        throwException_IfUsernameAlreadyExist(user.getUsername());

        // then update the user
        User updatedUser = this.userRepository.findUserById(id);
        updatedUser.setUsername(user.getUsername());
        updatedUser.setPassword(user.getPassword());
        this.userRepository.save(updatedUser);
    }

    @DeleteMapping
    public void deleteUser(@PathParam("user_id") long id) throws UserDoesntExist {
        //todo implement
        throwException_IfUserIdDoesntExist(id);
        // then delete user
        this.userRepository.deleteById(id);

    }

    private void throwException_IfUsernameAlreadyExist(String username) throws UserAlreadyExistException {
        if(userRepository.existsUserByUsername(username)){
            System.out.println("The username already exists");
            throw new UserAlreadyExistException(username);
        }
    }

    private void throwException_IfUserIdDoesntExist(Long id) throws UserDoesntExist {
        if(!userRepository.existsById(id)){
            //then user exists already
            throw new UserDoesntExist(id);
        }
    }


}
