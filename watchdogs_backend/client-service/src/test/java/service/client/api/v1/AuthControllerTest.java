package service.client.api.v1;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import service.client.config.RestAuthenticationEntryPoint;
import service.client.entities.User;
import service.client.repositories.UserRepository;
import service.client.service.ApiUserDetailsService;
import service.client.utils.Constants;


import javax.sql.DataSource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@WebMvcTest(AuthController.class)
public class AuthControllerTest extends SetUpForTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private PasswordEncoder passwordEncoder;


    @MockBean
    private UserRepository repository;

    @MockBean
    private ApiUserDetailsService apiUserDetailsService;

    @MockBean
    private RestAuthenticationEntryPoint authenticationEntryPoint;

    @Test
    public void test_Register_Successfull() throws Exception {
        BDDMockito.given(repository.findUserByUsername("newPerson")).
                willReturn(null);
        BDDMockito.when(passwordEncoder.encode("password")).
                then((invocationOnMock) -> invocationOnMock.getArgument(0));


        BDDMockito.when(repository.save(Mockito.any(User.class))).
                then((invocationOnMock) -> invocationOnMock.getArgument(0));

        mvc.perform(post("/register").
                param("username", "newPerson").param("password", "password")).
                andExpect(status().isCreated()).
                andExpect(jsonPath("$.username").value("newPerson")).
                andExpect(jsonPath("$.roles[0].type").value("REGULAR"));
    }

    @Test
    public void test_Register_UserExists() throws Exception {
        BDDMockito.given(repository.findUserByUsername("existingUser")).
                willReturn(new User("existingUser", "password"));

        mvc.perform(post("/register").
                param("username", "existingUser").
                param("password", "password")).
                andExpect(jsonPath("$.error.code").value(500)).
                andExpect(jsonPath("$.error.message").value(Constants.ErrorMsg.USER_ALREADY_EXISTS));
    }

    @Test
    public void test_Register_ConstraintViolationForParameters() throws Exception {
        BDDMockito.given(repository.findUserByUsername("admin")).
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
        BDDMockito.given(repository.findUserByUsername("admin")).
                willReturn(null);
        mvc.perform(post("/login")
                .param("username", "admin")
                .param("password", "admin123"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void test_Login_IncorrectPassword() throws Exception {
        BDDMockito.given(repository.findUserByUsername("newPerson")).
                willReturn(new User("newPerson", "newPerson"));
        //Simulating the case where encoded password is different.
        BDDMockito.when(passwordEncoder.encode("afds")).
                then((invocationOnMock) -> invocationOnMock.getArgument(0) + "xxx");
        BDDMockito.when(passwordEncoder.matches(Mockito.anyString(), Mockito.anyString()))
                .then(invocation -> invocation.getArgument(0).equals(invocation.getArgument(1)));

        mvc.perform(post("/login")
                .param("username", "newPerson")
                .param("password", "password"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.message").value(Constants.ErrorMsg.INCORRECT_PASSWORD));
    }

    @Test
    @WithMockUser(username = "test_user")
    public void test_Login_Successfull() throws Exception {
        BDDMockito.given(repository.findUserByUsername("existingUser")).
                willReturn(new User("existingUser", "password"));
        //Simulating the case where encoded password is different.
        BDDMockito.when(passwordEncoder.encode("password")).
                then((invocationOnMock) -> invocationOnMock.getArgument(0));
        BDDMockito.when(passwordEncoder.matches(Mockito.anyString(), Mockito.anyString()))
                .then(invocation -> invocation.getArgument(0).equals(invocation.getArgument(1)));

        mvc.perform(post("/login").
                param("username", "existingUser")
                .param("password", "password"))
                .andExpect(status().isCreated()).
                andExpect(jsonPath("$.username").value("existingUser")).
                andExpect(jsonPath("$.roles[0].type").value("REGULAR"));
    }

}