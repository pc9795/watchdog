package service.client.controllers;


import core.entities.cockroachdb.User;
import core.repostiories.cockroachdb.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import service.client.beans.UserLogin;
import service.client.utils.Constants;
import service.client.utils.Utils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

/**
 * Purpose: Controller for authentication
 */
@RestController
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Create a session of a user with given credentials.
     *
     * @param request   request object
     * @param userLogin user credentials
     * @return the logged in user
     * @throws ServletException if servlet API is not able to login user with the given credentials.
     */
    @PostMapping("/login")
    public User login(HttpServletRequest request, @Valid @RequestBody UserLogin userLogin)
            throws ServletException {
        //Check user exists in the database
        User user = userRepository.findUserByUsername(userLogin.getUsername());
        if (user == null) {
            throw new BadCredentialsException(String.format(Constants.ErrorMsg.USERNAME_NOT_FOUND,
                    userLogin.getUsername()));
        }
        //Check passwords
        if (!passwordEncoder.matches(userLogin.getPassword(), user.getPassword())) {
            throw new BadCredentialsException(Constants.ErrorMsg.PASSWORDS_NOT_MATCH);
        }
        //Create a session with the help of ServletAPI.
        request.login(userLogin.getUsername(), userLogin.getPassword());
        return user;
    }

    /**
     * Handles 404. There was not a clean way to handle it by a controller advice.
     *
     * @param response response object
     * @throws IOException if not able to update the response object.
     */
    @GetMapping("/error")
    public void handle404(HttpServletResponse response) throws IOException {
        Utils.createJSONErrorResponse(HttpServletResponse.SC_NOT_FOUND, Constants.ErrorMsg.NOT_FOUND, response);
    }
}
