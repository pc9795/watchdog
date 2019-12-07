package service.client.repositories;

import core.repostiories.cockroachdb.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import core.entities.cockroachdb.HttpMonitor;
import core.entities.cockroachdb.User;

import java.util.Arrays;
import java.util.Collections;

@RunWith(SpringRunner.class)
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    private User[] users;

    @Before
    public void setup() {
        users = new User[5];
        users[0] = new User("admin", "admin123");
        users[0].setMonitors(
                Collections.singletonList(
                        new HttpMonitor("testmonitor", "test ip address",
                                5, 80)));

        users[1] = new User("admin2", "admin123");
        users[1].setMonitors(Arrays.asList(
                new HttpMonitor("testmonitor1", "test ip address1", 5, 80),
                new HttpMonitor("testmonitor2", "test ip address2", 5, 80),
                new HttpMonitor("testmonitor3", "test ip address3", 5, 80)
        ));

        users[2] = new User("admin3", "admin123");
        users[2].setMonitors(Arrays.asList(
                new HttpMonitor("testmonitor4", "test ip address1", 5, 80),
                new HttpMonitor("testmonitor5", "test ip address2", 5, 80),
                new HttpMonitor("testmonitor6", "test ip address3", 5, 80)
        ));

        users[3] = new User("admin4", "admin123");
        users[3].setMonitors(Arrays.asList(
                new HttpMonitor("testmonitor7", "test ip address1", 5, 80),
                new HttpMonitor("testmonitor8", "test ip address2", 5, 80),
                new HttpMonitor("testmonitor9", "test ip address3", 5, 80)
        ));

        users[4] = new User("admin5", "admin123");
        users[4].setMonitors(Arrays.asList(
                new HttpMonitor("testmonitor1", "test ip address1", 5, 80),
                new HttpMonitor("testmonitor2", "test ip address2", 5, 80),
                new HttpMonitor("testmonitor3", "test ip address3", 5, 80)
        ));

        for (User user : users) {
            repository.save(user);
        }
    }

    @Test
    public void testFindUserByUsername() {
        assert repository.findUserByUsername("admin").getUsername().equals("admin");
        assert repository.findUserByUsername("wrongname") == null;
    }

    @Test
    public void testFindUserById() {
        assert repository.findById(1L).getId() == 1;
        assert repository.findById(-1L) == null;
    }

    @After
    public void clean() {
        repository.deleteAll();
    }
}