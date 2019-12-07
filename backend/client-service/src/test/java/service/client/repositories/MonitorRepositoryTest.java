package service.client.repositories;

import core.repostiories.cockroachdb.MonitorRepository;
import core.repostiories.cockroachdb.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import core.entities.cockroachdb.BaseMonitor;
import core.entities.cockroachdb.HttpMonitor;
import core.entities.cockroachdb.User;

import java.util.Arrays;

@RunWith(SpringRunner.class)
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MonitorRepositoryTest {

    @Autowired
    private MonitorRepository monitorRepository;
    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private BaseMonitor[] monitors;

    @Before
    public void setup() {
        testUser = new User("admin", "admin123");
        monitors = new BaseMonitor[5];
        monitors[0] = new HttpMonitor("testmonitor1", "test ip address1", 5, 80);
        monitors[1] = new HttpMonitor("testmonitor2", "test ip address2", 5, 80);
        monitors[2] = new HttpMonitor("testmonitor3", "test ip address3", 5, 80);
        monitors[3] = new HttpMonitor("testmonitor4", "test ip address4", 5, 80);
        monitors[4] = new HttpMonitor("testmonitor5", "test ip address5", 5, 80);

        // Will give it id of 1.
        for (BaseMonitor aMonitor : monitors) {
            testUser.addMonitor(aMonitor);
        }
        // Meals will have ids of 2, 3, 4, 5 respectively.
        userRepository.save(testUser);
        for (BaseMonitor aMonitor : monitors) {
            monitorRepository.save(aMonitor);
        }
    }

    @Test
    public void findById() {
        //BaseMonitor findById(long id);
        assert monitorRepository.findById(2).getId() == 2;
        assert monitorRepository.findById(-1) == null;
    }

    @Test
    public void findAll() {
        //boolean existsBaseMonitorById(long id);
        assert (monitorRepository.findAll().equals(Arrays.asList(monitors)));
    }
}