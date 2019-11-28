package service.client.api.v1;


import org.springframework.security.authentication.BadCredentialsException;
import service.client.entities.User;
import service.client.entities.UserRole;
import service.client.exceptions.UserAlreadyExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;

import org.springframework.web.bind.annotation.RestController;
import service.client.exceptions.UsernamePasswordIncorrect;
import service.client.repositories.UserRepository;
import service.client.utils.Constants;
import service.client.utils.Utils;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;

    @Autowired
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, Validator validator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User register(@RequestParam String username, @RequestParam String password)
            throws UserAlreadyExistException {
        System.out.println("gots here 1");

        if (userRepository.findUserByUsername(username) != null) {
            System.out.println("gots here 353: " + username);
            throw new UserAlreadyExistException();
        }
        System.out.println("gots here 2");

        User user = new User(username.trim(), password.trim());
        user.setRoles(Collections.singletonList(new UserRole(UserRole.UserRoleType.REGULAR)));
        System.out.println("gots here 3");

        //Check for any violations in constraints.
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            throw new ValidationException(Utils.joinCollection(
                    violations.stream().
                            map(violation -> violation.getPropertyPath().toString() + " " + violation.getMessage()).
                            collect(Collectors.toList()), ","));
        }
        System.out.println("gots here 4");

        //Encoding after checking validation
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    @PostMapping("/login")
    public User login(HttpServletRequest request, @RequestParam String username, @RequestParam String password)
            throws ServletException, UsernamePasswordIncorrect {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            System.out.println("user doesnt exist");
            throw new BadCredentialsException(Constants.ErrorMsg.BAD_CREDENTIALS);
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            System.out.println("passwords dont match : " + password + "  " + user.getPassword());
            throw new UsernamePasswordIncorrect();
        }
        System.out.println("passwords match : " +user.getId() + "   " + user.getUsername() + "  " + user.getPassword() + "   " + (user.getRoles().get(0)).getType());
        request.login(username, password);
        System.out.println("The user has succesfully logged in + " + user.getUsername() + "   " + user.getPassword());
        System.out.println("f");
        return user;
    }
}
