package service.client.api.v1;

import core.entities.cockroachdb.User;
import core.repostiories.cockroachdb.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import service.client.controllers.AuthController;
import service.client.service.ApiUserDetailsService;
import service.client.utils.Constants;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests user registering and login through the API and Repository layers
 *
 * @author Oisín Whelan 15558517
 */

@AutoConfigureMockMvc
@WebMvcTest(AuthController.class)
@ContextConfiguration
@WebAppConfiguration
public class UserAuthIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApiUserDetailsService apiUserDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testRegistrationAllLayers() throws Exception {
        User testUser = new User("user", "pass");
        int size = userRepository.findAll().size();

        mvc.perform(post("/register")
                .param("username", "user")
                .param("password", "pass"))
                .andExpect(status().isCreated());

        User registerUser = userRepository.findUserByUsername("user");
        Assert.assertNotNull(registerUser);
        Assert.assertEquals(testUser.getUsername(), registerUser.getUsername());
        Assert.assertEquals(testUser.getPassword(), registerUser.getPassword());

        Assert.assertEquals(userRepository.findAll().size(), size + 1);
    }

    @Test
    public void testRegistrationUserAlreadyExistsAllLayers() throws Exception {
        User testUser = new User("user", "pass");

        mvc.perform(post("/register")
                .param("username", "user")
                .param("password", "pass"))
                .andExpect(status().isCreated());

        int size = userRepository.findAll().size();

        mvc.perform(post("/register")
                .param("username", "user")
                .param("password", "pass"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.error.code").value(500))
                .andExpect(jsonPath("$.error.message").value(Constants.ErrorMsg.USER_ALREADY_EXISTS));

        Assert.assertEquals(userRepository.findAll().size(), size);

    }

    @Test
    public void testRegistrationAsAdminAllLayers() throws Exception {
        int size = userRepository.findAll().size();

        mvc.perform(post("/register").
                param("username", "admin").
                param("password", "admin")).
                andExpect(status().isBadRequest());

        Assert.assertEquals(size, userRepository.findAll().size());
    }
}
