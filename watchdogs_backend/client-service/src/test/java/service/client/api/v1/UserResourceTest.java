package service.client.api.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;
import service.client.config.RestAuthenticationEntryPoint;
import service.client.config.SecurityConfig;
import service.client.entities.User;
import service.client.entities.UserRole;
import service.client.repositories.UserRepository;
import service.client.service.ApiUserDetailsService;

import javax.sql.DataSource;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

    /**
     * Created By: Prashant Chaubey
     * Created On: 28-10-2019 04:12
     * Purpose: Test
     **/

    @RunWith(SpringRunner.class)
    @WebMvcTest(UserResource.class)
// Have to check this may be ContextConfiguration is overwriting all the context so that It is not able to find the
// resource without explicit configuration.
    @ContextConfiguration(classes = {RestAuthenticationEntryPoint.class, SecurityConfig.class, UserResource.class})
    public class UserResourceTest {

        @Autowired
        private MockMvc mvc;

        @MockBean
        private PasswordEncoder passwordEncoder;

        @MockBean
        private DataSource dataSource;

        @MockBean
        private UserRepository repository;

        @MockBean
        private ApiUserDetailsService apiUserDetailsService;

        private String testLoadWithTestUser;
        private User testUser;
        private String invalidPayload;
        private String adminPayload;
        private String testLoad;

        @Before
        public void setup() throws JsonProcessingException {
            testUser = new User("test", "test1234");
            testUser.setRoles(Collections.singletonList(new UserRole(UserRole.UserRoleType.REGULAR)));
            ObjectMapper mapper = new ObjectMapper();
            testLoadWithTestUser = mapper.writeValueAsString(testUser);

            // Creating JSON from object mapper will not work because we have used JsonIgnore for password filed.
            // Username is less than 5 characters.
            invalidPayload = "{\n" +
                    "    \"username\": \"test\",\n" +
                    "    \"password\": \"test1234\",\n" +
                    "    \"roles\": [\n" +
                    "        {\n" +
                    "            \"type\": \"REGULAR\"\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}";


            testLoad = "{\n" +
                    "    \"username\": \"test1234\",\n" +
                    "    \"password\": \"test1234\",\n" +
                    "    \"roles\": [\n" +
                    "        {\n" +
                    "            \"type\": \"REGULAR\"\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}";


        }

        @Test
        public void testWithoutAuthentication() throws Exception {
            mvc.perform(get("/api/v1/users")).
                    andExpect(status().isUnauthorized());

            mvc.perform(get("/api/v1/users/1")).
                    andExpect(status().isUnauthorized());

            mvc.perform(post("/api/v1/users").
                    contentType(MediaType.APPLICATION_JSON_UTF8).content(testLoadWithTestUser)).
                    andExpect(status().isUnauthorized());

            mvc.perform(put("/api/v1/users/1").
                    contentType(MediaType.APPLICATION_JSON_UTF8).content(testLoadWithTestUser)).
                    andExpect(status().isUnauthorized());

            mvc.perform(delete("/api/v1/users/1")).
                    andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(roles = {"REGULAR"})
        public void testDeleteWithRegular() throws Exception {
            mvc.perform(delete("/api/v1/users/1")).andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = {"REGULAR"})
        public void testUpdateWithRegular() throws Exception {
            mvc.perform(put("/api/v1/users/1").
                    contentType(MediaType.APPLICATION_JSON_UTF8).content(testLoadWithTestUser)).
                    andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = {"REGULAR"})
        public void testCreateWithRegular() throws Exception {
            mvc.perform(post("/api/v1/users").
                    contentType(MediaType.APPLICATION_JSON_UTF8).content(testLoadWithTestUser)).
                    andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = {"REGULAR"})
        public void testReadWithRegular() throws Exception {
            mvc.perform(get("/api/v1/users/1")).andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = {"REGULAR"})
        public void testReadAllWithRegular() throws Exception {
            mvc.perform(get("/api/v1/users/")).andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = {"USER_MANAGER"})
        public void testDeleteWithUserManager() throws Exception {
            BDDMockito.given(repository.findById(1L)).willReturn(java.util.Optional.ofNullable(testUser));
            mvc.perform(delete("/api/v1/users/1")).andExpect(status().isOk());
            Mockito.verify(repository, Mockito.times(1)).delete(testUser);
        }

        @Test(expected = NestedServletException.class)
        @WithMockUser(roles = {"USER_MANAGER"})
        public void testDeleteWithUserManagerAdminUser() throws Exception {
            User admin = new User("admin", "admin");
            admin.setRoles(Collections.singletonList(new UserRole(UserRole.UserRoleType.ADMIN)));
            BDDMockito.given(repository.findById(1L)).willReturn(java.util.Optional.ofNullable(testUser));

            mvc.perform(delete("/api/v1/users/1"));
        }

        @Test(expected = NestedServletException.class)
        @WithMockUser(roles = {"USER_MANAGER"})
        public void testDeleteWithUserManagerUserNotExist() throws Exception {
            BDDMockito.given(repository.findById(1L)).willReturn(java.util.Optional.ofNullable(testUser));

            mvc.perform(delete("/api/v1/users/1"));
        }

        @Test
        @WithMockUser(roles = {"USER_MANAGER"})
        public void testUpdateWithUserManager() throws Exception {
            BDDMockito.when(repository.findById(1L)).thenReturn(java.util.Optional.of(new User("xxxxx", "xxxxx")));
            BDDMockito.when(repository.save(Mockito.any(User.class))).then(
                    (invocation -> invocation.getArgument(0))
            );

            mvc.perform(put("/api/v1/users/1").
                    contentType(MediaType.APPLICATION_JSON_UTF8).
                    content(testLoad)).
                    andExpect(status().isOk()).
                    andExpect(jsonPath("$.username").value("test1234"));

        }

        @Test
        @WithMockUser(roles = {"USER_MANAGER"})
        public void testUpdateWithUserManagerUserNotExist() throws Exception {

            BDDMockito.when(repository.findById(1L)).thenReturn(null);
            BDDMockito.when(repository.save(Mockito.any(User.class))).then(
                    (invocation -> invocation.getArgument(0))
            );

            mvc.perform(put("/api/v1/users/1").
                    contentType(MediaType.APPLICATION_JSON_UTF8).
                    content(testLoad)).
                    andExpect(status().isOk()).
                    andExpect(jsonPath("$.username").value("test1234"));

        }


        @Test(expected = NestedServletException.class)
        @WithMockUser(roles = {"USER_MANAGER"})
        public void testUpdateWithUserManagerAdminUser() throws Exception {
            mvc.perform(put("/api/v1/users/1").
                    contentType(MediaType.APPLICATION_JSON_UTF8).
                    content(adminPayload));
        }


        @Test
        @WithMockUser(roles = {"USER_MANAGER"})
        public void testUpdateWithUserManagerInvalidUser() throws Exception {
            mvc.perform(put("/api/v1/users/1").
                    contentType(MediaType.APPLICATION_JSON_UTF8).
                    content(invalidPayload));
            // Validation API doesn't work with MockMvc so just checking that save shouldn't be called.
            BDDMockito.verify(repository, BDDMockito.times(0)).

                    save(Mockito.any(User.class));
        }


        @Test
        @WithMockUser(roles = {"USER_MANAGER"})
        public void testCreateWithUserManager() throws Exception {
            mvc.perform(post("/api/v1/users").
                    contentType(MediaType.APPLICATION_JSON_UTF8).
                    content(testLoad));
            Mockito.verify(repository, Mockito.times(1)).save(Mockito.any(User.class));
        }

        @Test(expected = NestedServletException.class)
        @WithMockUser(roles = {"USER_MANAGER"})
        public void testCreateWithUserManagerAdminUser() throws Exception {
            mvc.perform(post("/api/v1/users").
                    contentType(MediaType.APPLICATION_JSON_UTF8).
                    content(adminPayload));
        }


        @Test
        @WithMockUser(roles = {"USER_MANAGER"})
        public void testCreateWithUserManagerInvalidUser() throws Exception {
            mvc.perform(post("/api/v1/users").
                    contentType(MediaType.APPLICATION_JSON_UTF8).
                    content(invalidPayload));
            // Validation API doesn't work with MockMvc so just checking that save shouldn't be called.
            BDDMockito.verify(repository, BDDMockito.times(0)).save(Mockito.any(User.class));
        }

        @Test(expected = NestedServletException.class)
        @WithMockUser(roles = {"USER_MANAGER"})
        public void testReadWithUserManagerResourceNotExist() throws Exception {
            BDDMockito.given(repository.findById(1L)).willReturn(null);
            mvc.perform(get("/api/v1/users/1"));
        }

        @Test
        @WithMockUser(roles = {"USER_MANAGER"})
        public void testReadWithUserManager() throws Exception {
            BDDMockito.given(repository.findById(1L)).willReturn(java.util.Optional.ofNullable(testUser));
            mvc.perform(get("/api/v1/users/1")).
                    andExpect(status().isOk()).
                    andExpect(jsonPath("$.username").value("test"));
        }

        @Test
        @WithMockUser(roles = {"USER_MANAGER"})
        public void testReadAllWithUserManager() throws Exception {

            Page<User> dummyPage = new PageImpl<>(Collections.singletonList(testUser));
            BDDMockito.given(repository.findAll(Mockito.any(), Mockito.any(Pageable.class))).willReturn(dummyPage);

            mvc.perform(get("/api/v1/users")).
                    andExpect(status().isOk()).
                    andExpect(jsonPath("$").isArray()).
                    andExpect(jsonPath("$[0].username").value("test"));
        }

    }