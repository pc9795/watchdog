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
import service.client.entities.User;
import service.client.repositories.UserRepository;
import service.client.service.ApiUserDetailsService;
import service.client.utils.Constants;


import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
@WebMvcTest(AuthController.class)
@ContextConfiguration
@WebAppConfiguration
//@TestPropertySource(locations="classpath:test.properties")
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
    private ApiUserPrincipal apiUserPrincipal;

    @MockBean
    private ApiUserDetailsService apiUserDetailsService;

    @MockBean
    private RestAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private WebApplicationContext context;

    private User testUser;


    @Test
    public void test_Register_Successfull() throws Exception {
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
    public void test_Register_UserExists() throws Exception {
        User theUser = new User("existingUser", "password");
        List<UserRole> theListOfUsersRoles = new ArrayList<UserRole>();
        theListOfUsersRoles.add(new UserRole(UserRole.UserRoleType.REGULAR));
        theUser.setRoles(theListOfUsersRoles);

        BDDMockito.given(userRepository.findUserByUsername("existingUser")).
                willReturn(theUser);

        mvc.perform(post("/register").
                param("username", "existingUser").
                param("password", "password")).
                andExpect(jsonPath("$.error.code").value(500)).
                andExpect(jsonPath("$.error.message").value(Constants.ErrorMsg.USER_ALREADY_EXISTS));
    }

    @Test
    public void test_Register_ConstraintViolationForParameters() throws Exception {
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
    public void test_Login_UsernameNotFound() throws Exception {
        BDDMockito.given(userRepository.findUserByUsername("admin")).
                willReturn(null);
        mvc.perform(post("/login")
                .param("username", "admin")
                .param("password", "admin123"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void test_Login_IncorrectPassword() throws Exception {
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