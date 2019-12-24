package service.client.api.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import core.entities.cockroachdb.User;
import service.client.exceptions.ResourceNotFoundException;
import service.client.exceptions.UserAlreadyExistException;
import core.repostiories.cockroachdb.UserRepository;
import service.client.utils.Constants;

import javax.validation.Valid;

/**
 * Created By: Prashant Chaubey
 * Created On: 22-11-2019 00:32
 * Purpose: REST resource for accessing User.
 **/
@RestController
@RequestMapping(Constants.ApiV1Resource.USER)
public class UserResource {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserResource(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Create a user
     *
     * @param user user to be created
     * @return created user
     * @throws UserAlreadyExistException if user with given username already exist in database.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User addUser(@Valid @RequestBody User user) throws UserAlreadyExistException {
        if (userRepository.findUserByUsername(user.getUsername()) != null) {
            throw new UserAlreadyExistException(String.format(Constants.ErrorMsg.USER_ALREADY_EXIST,
                    user.getUsername()));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Create user
        return this.userRepository.save(user);
    }

    /**
     * Get a user with given id.
     *
     * @param userId db id of the user
     * @return user with the given id
     * @throws ResourceNotFoundException if user with given id doesn't exist in database.
     */
    @GetMapping("/{user_id}")
    public User getUserById(@PathVariable("user_id") long userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new ResourceNotFoundException(String.format("User id:%s", userId));
        }
        return user;
    }
}
