package service.client.api.v1;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import core.entities.cockroachdb.User;
import core.repostiories.cockroachdb.UserRepository;
import service.client.controllers.AuthController;
import service.client.service.ApiUserDetailsService;
import service.client.utils.Constants;

import javax.sql.DataSource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(AuthController.class)
@ContextConfiguration
@WebAppConfiguration
public class AuthControllerTest {
        @MockBean
        protected DataSource dataSource;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ApiUserDetailsService apiUserDetailsService;

    @Autowired
    private WebApplicationContext context;

    private User testUser;


    @Test
    public void testRegisterSuccessfull() throws Exception {
        BDDMockito.given(userRepository.findUserByUsername("existingUser")).
                willReturn(null);
        BDDMockito.when(passwordEncoder.encode("password")).
                then((invocationOnMock) -> invocationOnMock.getArgument(0));
        BDDMockito.when(userRepository.save(Mockito.any(User.class))).
                then((invocationOnMock) -> invocationOnMock.getArgument(0));

        mvc.perform(post("/register").
                param("username", "existingUser").param("password", "password")).
                andExpect(status().isCreated()).
                andExpect(jsonPath("$.username").value("existingUser")).
                andExpect(jsonPath("$.roles[0].type").value("REGULAR"));
    }

    @Test
    public void testRegisterUserExists() throws Exception {
        User theUser = new User("existingUser", "password");
        BDDMockito.given(userRepository.findUserByUsername("existingUser")).
                willReturn(theUser);

        mvc.perform(post("/register").
                param("username", "existingUser").
                param("password", "password")).
                andExpect(jsonPath("$.error.code").value(500)).
                andExpect(jsonPath("$.error.message").value(Constants.ErrorMsg.USER_ALREADY_EXISTS));
    }

    @Test
    public void testRegisterConstraintViolationForParameters() throws Exception {
        BDDMockito.given(userRepository.findUserByUsername("admin")).
                willReturn(null);
        BDDMockito.when(passwordEncoder.encode("admin")).
                then((invocationOnMock) -> invocationOnMock.getArgument(0));

        mvc.perform(post("/register").
                param("username", "admin").
                param("password", "admin")).
                andExpect(status().isBadRequest());
    }

    @Test
    public void testLoginUsernameNotFound() throws Exception {
        BDDMockito.given(userRepository.findUserByUsername("admin")).
                willReturn(null);
        mvc.perform(post("/login")
                .param("username", "admin")
                .param("password", "admin123"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testLoginIncorrectPassword() throws Exception {
        BDDMockito.given(userRepository.findUserByUsername("ferdia")).
                willReturn(new User("ferdia", "password"));
        //Simulating the case where encoded password is different.
        BDDMockito.when(passwordEncoder.encode("password")).
                then((invocationOnMock) -> invocationOnMock.getArgument(0));
        BDDMockito.when(passwordEncoder.matches(Mockito.anyString(), Mockito.anyString()))
                .then(invocation -> invocation.getArgument(0).equals(invocation.getArgument(1)));

        mvc.perform(post("/login")
                .param("username", "ferdia")
                .param("password", "incorrectpas"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.message").value(Constants.ErrorMsg.UNAUTHORIZED));
    }
}