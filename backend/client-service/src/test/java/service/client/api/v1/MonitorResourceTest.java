//package service.client.api.v1;
//
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//import service.client.config.RestAuthenticationEntryPoint;
//import service.client.config.SecurityConfig;
//import service.client.entities.BaseMonitor;
//import service.client.entities.HttpMonitor;
//import service.client.repositories.MonitorRepository;
//import service.client.service.ApiUserDetailsService;
//
//import javax.sql.DataSource;
//import java.time.LocalDate;
//import java.time.LocalTime;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@RunWith(SpringRunner.class)
//@WebMvcTest(MonitorResource.class)
//@ContextConfiguration(classes = {RestAuthenticationEntryPoint.class, SecurityConfig.class})
//class MonitorResourceTest extends SetUpForTest {
//
//    @Autowired
//    private MockMvc mvc;
//
//    @MockBean
//    private PasswordEncoder passwordEncoder;
//
//    @MockBean
//    private DataSource dataSource;
//
//    @MockBean
//    private MonitorRepository repository;
//
//    @MockBean
//    private ApiUserDetailsService apiUserDetailsService;
//
//    private HttpMonitor test_HttpMonitor;
//    private String test_HttpMonitor_LoadOf_testHttpMonitor;
//
//    private HttpMonitor test_SocketMonitor;
//    private String test_SocketMonitor_LoadOf_testSocketMonitor;
//
//    private HttpMonitor test_PingMonitor;
//    private String test_PingMonitor_LoadOf_testPingMonitor;
//
//    private HttpMonitor test_BaseMonitor;
//    private String test_BaseMonitor_LoadOf_testBaseMonitor;
//
//    @Test
//    public void test_GetUsersMonitorWithId_WithoutAuthentication() throws Exception {
//        mvc.perform(get("/api/v1/users/0/1")).
//                andExpect(status().isUnauthorized());
//
//        mvc.perform(get("/api/v1/meals/1")).
//                andExpect(status().isUnauthorized());
//
//        HttpMon meal = new Meal();
//        meal.setDate(LocalDate.now());
//        meal.setTime(LocalTime.now());
//        meal.setText("test_food");
//        meal.setCalories(1000);
//        ObjectMapper mapper = new ObjectMapper();
//        String jsonLoad = mapper.writeValueAsString(meal);
//
//        mvc.perform(post("/api/v1/meals").
//                contentType(MediaType.APPLICATION_JSON_UTF8).content(jsonLoad)).
//                andExpect(status().isUnauthorized());
//
//        mvc.perform(put("/api/v1/meals/1").
//                contentType(MediaType.APPLICATION_JSON_UTF8).content(jsonLoad)).
//                andExpect(status().isUnauthorized());
//
//        mvc.perform(delete("/api/v1/meals/1")).
//                andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    @WithMockUser(roles = {"USER_MANAGER"})
//    public void testDeleteWithUserManager() throws Exception {
//        mvc.perform(delete("/api/v1/meals/1")).andExpect(status().isForbidden());
//    }
//
//    @Test
//    @WithMockUser(roles = {"USER_MANAGER"})
//    public void testUpdateWithUserManager() throws Exception {
//        mvc.perform(put("/api/v1/meals/1").
//                contentType(MediaType.APPLICATION_JSON_UTF8).content(testMealLoadOftestMeal)).
//                andExpect(status().isForbidden());
//    }
//
//    @Test
//    @WithMockUser(roles = {"USER_MANAGER"})
//    public void testCreateWithUserManager() throws Exception {
//        mvc.perform(post("/api/v1/meals").
//                contentType(MediaType.APPLICATION_JSON_UTF8).content(testMealLoadOftestMeal)).
//                andExpect(status().isForbidden());
//    }
//
//    @Test
//    @WithMockUser(roles = {"USER_MANAGER"})
//    public void testReadWithUserManager() throws Exception {
//        mvc.perform(get("/api/v1/meals/1")).andExpect(status().isForbidden());
//    }
//
//    @Test
//    @WithMockUser(roles = {"USER_MANAGER"})
//    public void testReadAllWithUserManager() throws Exception {
//        mvc.perform(get("/api/v1/meals/")).andExpect(status().isForbidden());
//    }
//}