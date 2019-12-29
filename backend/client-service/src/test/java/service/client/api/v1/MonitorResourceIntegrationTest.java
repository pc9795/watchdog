package service.client.api.v1;

import core.entities.cockroachdb.BaseMonitor;
import core.entities.cockroachdb.User;
import core.repostiories.cockroachdb.MonitorRepository;
import core.repostiories.cockroachdb.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import service.client.service.ApiUserDetailsService;
import service.client.utils.Constants;

import java.security.Principal;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests creating and accessing monitors through the API and Repository layers
 * @author Oisín Whelan 15558517
 */
@WebMvcTest(MonitorResource.class)
@AutoConfigureMockMvc
@ContextConfiguration
@WebAppConfiguration
public class MonitorResourceIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MonitorRepository monitorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApiUserDetailsService service;

    @Mock
    private Principal principal;

    private BaseMonitor monitor;

    @Test
    public void testGetAllMonitorsAllLayers() throws Exception{
        User user = new User("user", "pass");
        BaseMonitor monitor = new BaseMonitor("test", "ip", 500);
        monitor.setUser(user);

        BDDMockito.when(principal.getName()).thenReturn("user");

        monitorRepository.saveAndFlush(monitor);

        mvc.perform(get(""))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void testGetMonitorDoesntExistAllLayers() throws Exception{
        monitorRepository.deleteAll();

        BDDMockito.when(principal.getName()).thenReturn("user");

        mvc.perform(get("/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.message").value(Constants.ErrorMsg.NOT_FOUND));
    }

    @Test
    public void testGetMonitorNotAuthorisedAllLayers() throws Exception{
        User user = new User("user", "pass");
        BaseMonitor monitor = new BaseMonitor("test", "ip", 500);
        monitor.setUser(user);
        monitor.setId(1L);
        monitorRepository.saveAndFlush(monitor);

        BDDMockito.when(principal.getName()).thenReturn("user2");

        mvc.perform(get("/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.message").value(Constants.ErrorMsg.FORBIDDEN_RESOURCE));
    }

    @Test
    public void testGetMonitorByIdAllLayers() throws Exception{
        User user = new User("user", "pass");
        BaseMonitor monitor = new BaseMonitor("test", "ip", 500);
        monitor.setUser(user);
        monitor.setId(1L);
        monitorRepository.saveAndFlush(monitor);

        BDDMockito.when(principal.getName()).thenReturn("user");

        mvc.perform(get("/1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.name").value("user"));
    }

    @Test
    public void testCreateMonitorAllLayers() throws Exception{
        int size = monitorRepository.findAll().size();

        BDDMockito.when(principal.getName()).thenReturn("user");

        mvc.perform(post("")
                .param("id", "1")
                .param("ipOrHost", "ip")
                .param("monitoringInterval", "500"))
                .andExpect(status().isCreated());

        Assert.assertEquals(size + 1, monitorRepository.findAll().size());
    }

    @Test
    public void testCreateMonitorLowMonitoringIntervalAllLayers() throws Exception{

        int size = monitorRepository.findAll().size();

        BDDMockito.when(principal.getName()).thenReturn("user");

        mvc.perform(post("")
                .param("id", "1")
                .param("ipOrHost", "ip")
                .param("monitoringInterval", "50"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value(Constants.ErrorMsg.BAD_REQUEST));

        Assert.assertEquals(size, monitorRepository.findAll().size());
    }

    @Test
    public void testUpdateMonitorLowMonitoringIntervalAllLayers() throws Exception{
        User user = new User("user", "pass");
        BaseMonitor monitor = new BaseMonitor("test", "ip", 500);
        monitor.setUser(user);
        monitor.setId(1L);
        monitorRepository.saveAndFlush(monitor);

        BDDMockito.when(principal.getName()).thenReturn("user");

        mvc.perform(put("/1")
                .param("id", "1")
                .param("ipOrHost", "ip")
                .param("monitoringInterval", "50"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value(Constants.ErrorMsg.BAD_REQUEST));

        Assert.assertEquals(500, monitorRepository.findById(1L).getMonitoringInterval());

    }

    @Test
    public void testUpdateMonitorNoMonitorFoundAllLayers() throws Exception{
        monitorRepository.deleteAll();

        BDDMockito.when(principal.getName()).thenReturn("user");

        mvc.perform(put("/1")
                .param("id", "1")
                .param("ipOrHost", "ip")
                .param("monitoringInterval", "500"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.message").value(Constants.ErrorMsg.NOT_FOUND));
    }

    @Test
    public void testUpdateMonitorNotAuthorisedAllLayers() throws Exception{
        User user = new User("user", "pass");
        BaseMonitor monitor = new BaseMonitor("test", "url", 500);
        monitor.setUser(user);
        monitor.setId(1L);
        monitorRepository.saveAndFlush(monitor);

        BDDMockito.when(principal.getName()).thenReturn("user2");

        mvc.perform(put("/1")
                .param("id", "1")
                .param("ipOrHost", "ip")
                .param("monitoringInterval", "500"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.message").value(Constants.ErrorMsg.FORBIDDEN_RESOURCE));

        Assert.assertEquals("url", monitorRepository.findById(1L).getIpOrHost());
    }

    @Test
    public void testUpdateMonitorAllLayers() throws Exception{
        User user = new User("user", "pass");
        BaseMonitor monitor = new BaseMonitor("test", "ip", 500);
        monitor.setUser(user);
        monitor.setId(1L);
        monitorRepository.saveAndFlush(monitor);

        BDDMockito.when(principal.getName()).thenReturn("user");

        mvc.perform(put("/1")
                .param("id", "1")
                .param("ipOrHost", "ip")
                .param("monitoringInterval", "5000"))
                .andExpect(status().is2xxSuccessful());

        Assert.assertEquals(5000, monitorRepository.findById(1L).getMonitoringInterval());

    }

    @Test
    public void testDeleteMonitorNotFoundAllLayers() throws Exception{
        User user = new User("user", "pass");
        BaseMonitor monitor = new BaseMonitor("test", "ip", 500);
        monitor.setUser(user);
        monitor.setId(1L);
        monitorRepository.saveAndFlush(monitor);

        int size = monitorRepository.findAll().size();

        BDDMockito.when(principal.getName()).thenReturn("user");

        mvc.perform(delete("/2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.message").value(Constants.ErrorMsg.NOT_FOUND));

        Assert.assertEquals(size, monitorRepository.findAll().size());
    }

    @Test
    public void testDeleteMonitorNotAuthorisedAllLayers() throws Exception{
        User user = new User("user", "pass");
        BaseMonitor monitor = new BaseMonitor("test", "ip", 500);
        monitor.setUser(user);
        monitor.setId(1L);
        monitorRepository.saveAndFlush(monitor);

        BDDMockito.when(principal.getName()).thenReturn("user2");
        int size = monitorRepository.findAll().size();

        mvc.perform(delete("/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.message").value(Constants.ErrorMsg.FORBIDDEN_RESOURCE));

        Assert.assertEquals(size, monitorRepository.findAll().size());
    }

    @Test
    public void testDeleteMonitorAllLayers() throws Exception{
        User user = new User("user", "pass");
        BaseMonitor monitor = new BaseMonitor("test", "ip", 500);
        monitor.setUser(user);
        monitor.setId(1L);
        monitorRepository.saveAndFlush(monitor);

        int size = monitorRepository.findAll().size();

        BDDMockito.when(principal.getName()).thenReturn("user");

        mvc.perform(delete("/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.message").value(Constants.ErrorMsg.NOT_FOUND));

        Assert.assertEquals(size - 1, monitorRepository.findAll().size());

    }
}
