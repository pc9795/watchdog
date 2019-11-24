package client_service.api.v1;

import client_service.entities.User;
import client_service.exceptions.ResourceNotExistException;
import client_service.exceptions.UserAlreadyExistException;
import client_service.exceptions.UserDoesntExist;
import client_service.repositories.UserRepository;
import client_service.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.websocket.server.PathParam;

/**
 * Created By: Prashant Chaubey
 * Created On: 22-11-2019 00:32
 * Purpose: TODO:
 **/
@RestController
@RequestMapping(Constants.ApiV1Resource.USER)
public class UserResource {

    private final UserRepository userRepository;

    @Autowired
    public UserResource(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //@RequestMapping(value="/applications",method= RequestMethod.POST)
    @PostMapping
    public User addUser(@PathParam("user") @Valid User user) throws UserAlreadyExistException {
        //todo implement
        throwException_IfUsernameAlreadyExist(user.getUsername());
        // Create user
        userRepository.save(user);

        return user;
    }
    //("/{user_id}")
    @GetMapping
    public User getUser(@PathParam("user_id") long id) throws UserDoesntExist {
        //todo implement
        throwException_IfUserIdDoesntExist(id);

        return userRepository.findUserById(id);
    }

    @PutMapping()
    public void updateUser(@PathParam("user_id") long id, @PathParam("user") User user) throws UserDoesntExist, UserAlreadyExistException {
        //todo implement
        throwException_IfUserIdDoesntExist(id);
        throwException_IfUsernameAlreadyExist(user.getUsername());

        // then update the user
        User updatedUser = userRepository.findUserById(id);
        updatedUser.setUsername(user.getUsername());
        updatedUser.setPassword(user.getPassword());
        userRepository.save(updatedUser);
    }

    @DeleteMapping
    public void deleteUser(@PathParam("user_id") long id) throws UserDoesntExist {
        //todo implement
        throwException_IfUserIdDoesntExist(id);
        // then delete user
        userRepository.deleteById(id);

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
