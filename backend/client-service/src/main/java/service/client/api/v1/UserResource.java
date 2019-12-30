package service.client.api.v1;

import core.entities.cockroachdb.User;
import core.repostiories.cockroachdb.UserRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import service.client.exceptions.ResourceNotFoundException;
import service.client.exceptions.UserAlreadyExistException;
import service.client.utils.Constants;

import javax.validation.Valid;

/**
 * Purpose: REST resource for accessing User.
 **/
@RestController
@RequestMapping(Constants.ApiV1Resource.USER)
@Api(value = "User resource", description = "Operations pertaining to manipulate users in Watchdog")
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
    @ApiOperation(value = "Add an user")
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
    @ApiOperation(value = "Get an user by its database id")
    public User getUserById(@PathVariable("user_id") long userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new ResourceNotFoundException(String.format("User id:%s", userId));
        }
        return user;
    }
}
