package service.client.api.v1;

import core.entities.cockroachdb.BaseMonitor;
import core.entities.cockroachdb.User;
import core.repostiories.cockroachdb.MonitorRepository;
import core.repostiories.cockroachdb.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import service.client.service.ApiUserDetailsService;
import service.client.utils.Constants;

import javax.sql.DataSource;
import java.security.Principal;
import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests the methods and exceptions used by the MonitorResource endpoint
 *
 * @author Oisín Whelan 15558517
 */

@RunWith(SpringRunner.class)
@WebMvcTest(MonitorResource.class)
@ContextConfiguration
@WebAppConfiguration
public class MonitorResourceTest {

    @MockBean
    protected DataSource dataSource;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private MonitorRepository monitorRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ApiUserDetailsService service;

    @Mock
    private Principal principal;

    @Mock
    private User user;

    @Mock
    private BaseMonitor monitor;

    @Test
    public void testGetAllMonitors() throws Exception {
        ArrayList<BaseMonitor> list = new ArrayList<>();
        list.add(new BaseMonitor());
        BDDMockito.given(userRepository.findUserByUsername("user"))
                .willReturn(user);
        BDDMockito.given(monitorRepository.findAllByUser(user))
                .willReturn(list);
        BDDMockito.when(principal.getName()).thenReturn("user");

        mvc.perform(get(""))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testGetMonitorDoesntExist() throws Exception {
        BDDMockito.given(monitorRepository.findById(1L))
                .willReturn(null);
        BDDMockito.given(userRepository.findUserByUsername("user"))
                .willReturn(user);
        BDDMockito.when(principal.getName()).thenReturn("user");

        mvc.perform(get("/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.message").value(Constants.ErrorMsg.NOT_FOUND));
    }

    @Test
    public void testGetMonitorNotAuthorised() throws Exception {
        BDDMockito.given(monitorRepository.findById(1L))
                .willReturn(monitor);
        BDDMockito.given(userRepository.findUserByUsername("user"))
                .willReturn(user);
        BDDMockito.given(monitor.getUser().getUsername())
                .willReturn("wrong");
        BDDMockito.when(principal.getName()).thenReturn("user");

        mvc.perform(get("/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.message").value(Constants.ErrorMsg.FORBIDDEN_RESOURCE));
    }

    @Test
    public void testGetMonitorById() throws Exception {
        BDDMockito.given(userRepository.findUserByUsername("user"))
                .willReturn(user);
        BDDMockito.when(principal.getName()).thenReturn("user");
        BDDMockito.given(monitorRepository.findById(1L))
                .willReturn(monitor);
        BDDMockito.given(monitor.getUser().getUsername())
                .willReturn("user");

        mvc.perform(get("/1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.name").value("user"));
    }

    @Test
    public void testCreateMonitor() throws Exception {
        BDDMockito.given(userRepository.findUserByUsername("user"))
                .willReturn(user);
        BDDMockito.when(principal.getName()).thenReturn("user");

        mvc.perform(post("")
                .param("id", "1")
                .param("ipOrHost", "ip")
                .param("monitoringInterval", "500"))
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreateMonitorLowMonitoringInterval() throws Exception {
        BDDMockito.given(userRepository.findUserByUsername("user"))
                .willReturn(user);
        BDDMockito.when(principal.getName()).thenReturn("user");

        mvc.perform(post("")
                .param("id", "1")
                .param("ipOrHost", "ip")
                .param("monitoringInterval", "50"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value(Constants.ErrorMsg.BAD_REQUEST));
    }

    @Test
    public void testUpdateMonitorLowMonitoringInterval() throws Exception {
        BDDMockito.given(userRepository.findUserByUsername("user"))
                .willReturn(user);
        BDDMockito.when(principal.getName()).thenReturn("user");
        BDDMockito.when(monitorRepository.findById(1L))
                .thenReturn(monitor);
        BDDMockito.given(monitor.getUser().getUsername())
                .willReturn("user");

        mvc.perform(put("/1")
                .param("id", "1")
                .param("ipOrHost", "ip")
                .param("monitoringInterval", "50"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value(Constants.ErrorMsg.BAD_REQUEST));

    }

    @Test
    public void testUpdateMonitorNoMonitorFound() throws Exception {
        BDDMockito.given(userRepository.findUserByUsername("user"))
                .willReturn(user);
        BDDMockito.when(principal.getName()).thenReturn("user");
        BDDMockito.when(monitorRepository.findById(1L))
                .thenReturn(null);

        mvc.perform(put("/1")
                .param("id", "1")
                .param("ipOrHost", "ip")
                .param("monitoringInterval", "500"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.message").value(Constants.ErrorMsg.NOT_FOUND));
    }

    @Test
    public void testUpdateMonitorNotAuthorised() throws Exception {
        BDDMockito.given(userRepository.findUserByUsername("user"))
                .willReturn(user);
        BDDMockito.when(principal.getName()).thenReturn("user");
        BDDMockito.when(monitorRepository.findById(1L))
                .thenReturn(monitor);
        BDDMockito.given(monitor.getUser().getUsername())
                .willReturn("wrong");

        mvc.perform(put("/1")
                .param("id", "1")
                .param("ipOrHost", "ip")
                .param("monitoringInterval", "500"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.message").value(Constants.ErrorMsg.FORBIDDEN_RESOURCE));
    }

    @Test
    public void testUpdateMonitor() throws Exception {
        BDDMockito.given(userRepository.findUserByUsername("user"))
                .willReturn(user);
        BDDMockito.when(principal.getName()).thenReturn("user");
        BDDMockito.when(monitorRepository.findById(1L))
                .thenReturn(monitor);
        BDDMockito.given(monitor.getUser().getUsername())
                .willReturn("user");

        mvc.perform(put("/1")
                .param("id", "1")
                .param("ipOrHost", "ip")
                .param("monitoringInterval", "500"))
                .andExpect(status().is2xxSuccessful());

    }

    @Test
    public void testDeleteMonitorNotFound() throws Exception {
        BDDMockito.given(userRepository.findUserByUsername("user"))
                .willReturn(user);
        BDDMockito.when(principal.getName()).thenReturn("user");
        BDDMockito.when(monitorRepository.findById(1L))
                .thenReturn(null);

        mvc.perform(delete("/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.message").value(Constants.ErrorMsg.NOT_FOUND));
    }

    @Test
    public void testDeleteMonitorNotAuthorised() throws Exception {
        BDDMockito.given(userRepository.findUserByUsername("user"))
                .willReturn(user);
        BDDMockito.when(principal.getName()).thenReturn("user");
        BDDMockito.when(monitorRepository.findById(1L))
                .thenReturn(monitor);
        BDDMockito.given(monitor.getUser().getUsername())
                .willReturn("wrong");

        mvc.perform(delete("/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.message").value(Constants.ErrorMsg.FORBIDDEN_RESOURCE));
    }
}
