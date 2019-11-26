package client_service.api.v1;

import client_service.entities.*;
import client_service.exceptions.MonitorDoesExistOrDoesNotBelongToUser;
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
    @PostMapping("/create_user")
    public User addUser(@PathParam("user") @Valid User user) throws UserAlreadyExistException {
        //todo implement
        throwException_IfUsernameAlreadyExist(user.getUsername());
        // Create user
        this.userRepository.save(user);

        return user;
    }

    @GetMapping("/{user_id}")
    public User getUser(@PathVariable("user_id") long id) throws UserDoesntExist {
        throwException_IfUserIdDoesntExist(id);

        return this.userRepository.findUserById(id);
    }

    @PutMapping("/{user_id}/updateUser")
    public void updateUser(@PathVariable("user_id") long id, @PathParam("user") User user) throws UserDoesntExist, UserAlreadyExistException {
        //todo: this allows user to update username and password, mite need more
        throwException_IfUserIdDoesntExist(id);
        throwException_IfUsernameAlreadyExist(user.getUsername());

        // then update the user
        User updatedUser = this.userRepository.findUserById(id);
        updatedUser.setUsername(user.getUsername());
        updatedUser.setPassword(user.getPassword());
        this.userRepository.save(updatedUser);
    }

    @DeleteMapping("/{user_id}/deleteUser")
    public void deleteUser(@PathVariable("user_id") long id) throws UserDoesntExist {
        throwException_IfUserIdDoesntExist(id);
        // then delete user
        this.userRepository.deleteById(id);

    }

    // Adding monitor Methods:
    @PostMapping("/{user_id}/addMonitor/http")
    public HttpMonitor addHttpMonitorToUser(@PathVariable("user_id") long userId, @PathParam("http_monitor") @Valid HttpMonitor httpMonitor) throws UserDoesntExist {
        return (HttpMonitor) addMonitorToUser(userId,httpMonitor);
    }

    @PostMapping("/{user_id}/addMonitor/socket")
    public SocketMonitor addSocketMonitorToUser(@PathVariable("user_id") long userId, @PathParam("socket_monitor") @Valid SocketMonitor socketMonitor) throws UserDoesntExist {
        return (SocketMonitor) addMonitorToUser(userId,socketMonitor);

    }

    @PostMapping("/{user_id}/addMonitor/ping")
    public PingMonitor addPingMonitorToUser(@PathVariable("user_id") long userId, @PathParam("ping_monitor") @Valid PingMonitor pingMonitor) throws UserDoesntExist {
        return (PingMonitor) addMonitorToUser(userId,pingMonitor);
    }

    private BaseMonitor addMonitorToUser(long userId, BaseMonitor monitor) throws UserDoesntExist {
        if(!userRepository.existsById(userId)){
            throw new UserDoesntExist(userId);
        }
        // Then no error, so add the monitor
        User userWithId = userRepository.findUserById(userId);
        BaseMonitor theNewMonitor = monitorRepository.createMonitor(userWithId,monitor);
        userWithId.addMonitor(theNewMonitor);
        return (BaseMonitor) theNewMonitor;
    }

    // end of adding monitor methods
    // deleteing monitor methods
    @DeleteMapping("/{user_id}/deleteMonitor/{monitor_id}")
    public void deleteAUsersMonitor(@PathVariable("user_id") long userId, @PathVariable("monitor_id") long monitorId ) throws MonitorDoesExistOrDoesNotBelongToUser {
        if(monitorRepository.existsById(monitorId)){
            BaseMonitor theMonitorToDelete = monitorRepository.findById(monitorId);
            if(theMonitorToDelete.getUser().getId() == userId){
                monitorRepository.deleteById(monitorId);
                return;
            }
        }
        // Else throw exception
        throw new MonitorDoesExistOrDoesNotBelongToUser(userId,monitorId);
    }



    // end of deleting monitor methods

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
