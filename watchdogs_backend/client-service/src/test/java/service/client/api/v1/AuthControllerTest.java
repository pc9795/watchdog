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
import org.springframework.test.web.servlet.ResultActions;
import service.client.config.RestAuthenticationEntryPoint;
import service.client.entities.User;
import service.client.entities.UserRole;
import service.client.repositories.UserRepository;
import service.client.service.ApiUserDetailsService;
import service.client.utils.Constants;


import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@WebMvcTest(AuthController.class)
//@TestPropertySource(locations="classpath:test.properties")
public class AuthControllerTest {
        @MockBean
        protected DataSource dataSource;

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

//    @Test
//    public void test_Register_Successfull() throws Exception {
//        BDDMockito.given(repository.findUserByUsername("existingUser")).
//                willReturn(null);
//
//        BDDMockito.when(passwordEncoder.encode("password")).
//                then((invocationOnMock) -> invocationOnMock.getArgument(0));
//        BDDMockito.when(repository.save(Mockito.any(User.class))).
//                then((invocationOnMock) -> invocationOnMock.getArgument(0));
//
//        mvc.perform(post("/register").
//                param("username", "existingUser").param("password", "password")).
//                andExpect(status().isCreated()).
//                andExpect(jsonPath("$.username").value("existingUser")).
//                andExpect(jsonPath("$.roles[0].type").value("REGULAR"));
//    }
//
//    @Test
//    public void test_Register_UserExists() throws Exception {
//        User theUser = new User("existingUser", "password");
//        List<UserRole> theListOfUsersRoles = new ArrayList<UserRole>();
//        theListOfUsersRoles.add(new UserRole(UserRole.UserRoleType.REGULAR));
//        theUser.setRoles(theListOfUsersRoles);
//
//        BDDMockito.given(repository.findUserByUsername("existingUser")).
//                willReturn(theUser);
//
//        mvc.perform(post("/register").
//                param("username", "existingUser").
//                param("password", "password")).
//                andExpect(jsonPath("$.error.code").value(500)).
//                andExpect(jsonPath("$.error.message").value(Constants.ErrorMsg.USER_ALREADY_EXISTS));
//    }
//
//    @Test
//    public void test_Register_ConstraintViolationForParameters() throws Exception {
//        BDDMockito.given(repository.findUserByUsername("admin")).
//                willReturn(null);
//        BDDMockito.when(passwordEncoder.encode("admin")).
//                then((invocationOnMock) -> invocationOnMock.getArgument(0));
//
//        mvc.perform(post("/register").
//                param("username", "admin").
//                param("password", "admin")).
//                andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void test_Login_UsernameNotFound() throws Exception {
//        BDDMockito.given(repository.findUserByUsername("admin")).
//                willReturn(null);
//        mvc.perform(post("/login")
//                .param("username", "admin")
//                .param("password", "admin123"))
//                .andExpect(status().isBadRequest());
//    }

//    @Test
//    public void test_Login_IncorrectPassword() throws Exception {
//        BDDMockito.given(repository.findUserByUsername("ferdia")).
//                willReturn(new User("ferdia", "password"));
//        //Simulating the case where encoded password is different.
//        BDDMockito.when(passwordEncoder.encode("password")).
//                then((invocationOnMock) -> invocationOnMock.getArgument(0));
//        BDDMockito.when(passwordEncoder.matches(Mockito.anyString(), Mockito.anyString()))
//                .then(invocation -> invocation.getArgument(0).equals(invocation.getArgument(1)));
//
//        mvc.perform(post("/login")
//                .param("username", "ferdia")
//                .param("password", "incorrectpas"))
//                .andExpect(status().isUnauthorized())
//                .andExpect(jsonPath("$.error.message").value(Constants.ErrorMsg.UNAUTHORIZED));
//    }

    @Test
    public void test_Login_Successfull() throws Exception {

        BDDMockito.given(repository.findUserByUsername("existingUser")).
                willReturn(null);

        BDDMockito.when(passwordEncoder.encode("password")).
                then((invocationOnMock) -> invocationOnMock.getArgument(0));
        BDDMockito.when(repository.save(Mockito.any(User.class))).
                then((invocationOnMock) -> invocationOnMock.getArgument(0));

        mvc.perform(post("/register").
                param("username", "existingUser").param("password", "password")).
                andExpect(status().isCreated()).
                andExpect(jsonPath("$.username").value("existingUser")).
                andExpect(jsonPath("$.roles[0].type").value("REGULAR"));







//        User theUser = new User("existingUser", "password");
//        List<UserRole> theListOfUsersRoles = new ArrayList<UserRole>();
//        theListOfUsersRoles.add(new UserRole(UserRole.UserRoleType.ADMIN));
//        theUser.setRoles(theListOfUsersRoles);
//
//        BDDMockito.given(repository.findUserByUsername("existingUser")).
//                willReturn(theUser);
//
//        //Simulating the case where encoded password is different.
//        BDDMockito.when(passwordEncoder.encode("password")).
//                then((invocationOnMock) -> invocationOnMock.getArgument(0));
//        BDDMockito.when(passwordEncoder.matches(Mockito.anyString(), Mockito.anyString()))
//                .then(invocation -> invocation.getArgument(0).equals(invocation.getArgument(1)));



//        BDDMockito.when(passwordEncoder.matches(Mockito.anyString(), Mockito.anyString()))
//                .then(invocation -> invocation.getArgument(0).equals(invocation.getArgument(1)));

        mvc.perform(post("/login")
                .param("username", "ferdia")
                .param("password", "password"))
                .andExpect(jsonPath("$.username").value("ferdia"))
                .andExpect(jsonPath("$.roles[0].type").value("ADMIN"));
        //System.out.println(results);
    }

}