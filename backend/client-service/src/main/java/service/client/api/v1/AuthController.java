package service.client.api.v1;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import service.client.beans.UserLogin;
import service.client.entities.User;
import service.client.repositories.UserRepository;
import service.client.utils.Constants;
import service.client.utils.Utils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@RestController
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public User login(HttpServletRequest request, @Valid @RequestBody UserLogin userLogin)
            throws ServletException {
        User user = userRepository.findUserByUsername(userLogin.getUsername());
        if (user == null) {
            throw new BadCredentialsException(String.format(Constants.ErrorMsg.USERNAME_NOT_FOUND,
                    userLogin.getUsername()));
        }
        if (!passwordEncoder.matches(userLogin.getPassword(), user.getPassword())) {
            throw new BadCredentialsException(Constants.ErrorMsg.PASSWORDS_NOT_MATCH);
        }
        request.login(userLogin.getUsername(), userLogin.getPassword());
        return user;
    }

    /**
     * Handles 404. There was not a clean way to handle it by a controller advice.
     *
     * @param response
     * @throws IOException
     */
    @GetMapping("/error")
    public void handle404(HttpServletResponse response) throws IOException {
        Utils.createJSONErrorResponse(HttpServletResponse.SC_NOT_FOUND, Constants.ErrorMsg.NOT_FOUND, response);
    }
}