package service.client.repositories;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import service.client.entities.BaseMonitor;
import service.client.entities.HttpMonitor;
import service.client.entities.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private UserRepository repository;

    private User[] users;


    @Before
    public void setup() {
        users = new User[5];
        users[0] = new User("admin", "admin123");
        List<BaseMonitor> user_0_Monitors = new ArrayList<>();
        user_0_Monitors.add(new HttpMonitor("testmonitor","test ip address", 5, 80));
        users[0].setMonitors(user_0_Monitors);
        users[0].setRoles(Collections.singletonList(new UserRole(UserRole.UserRoleType.REGULAR)));

        users[1] = new User("admin2", "admin123");
        List <BaseMonitor> user_1_Monitors = new ArrayList<>();
        user_1_Monitors.add(new HttpMonitor("testmonitor1","test ip address1", 5, 80));
        user_1_Monitors.add(new HttpMonitor("testmonitor2","test ip address2", 5, 80));
        user_1_Monitors.add(new HttpMonitor("testmonitor3","test ip address3", 5, 80));
        users[1].setMonitors(user_0_Monitors);
        users[1].setRoles(Collections.singletonList(new UserRole(UserRole.UserRoleType.REGULAR)));

        users[2] = new User("admin3", "admin123");
        List <BaseMonitor> user_2_Monitors = new ArrayList<>();
        user_2_Monitors.add(new HttpMonitor("testmonitor4","test ip address1", 5, 80));
        user_2_Monitors.add(new HttpMonitor("testmonitor5","test ip address2", 5, 80));
        user_2_Monitors.add(new HttpMonitor("testmonitor6","test ip address3", 5, 80));
        users[2].setRoles(Collections.singletonList(new UserRole(UserRole.UserRoleType.REGULAR)));

        users[3] = new User("admin4", "admin123");
        List <BaseMonitor> user_3_Monitors = new ArrayList<>();
        user_3_Monitors.add(new HttpMonitor("testmonitor7","test ip address1", 5, 80));
        user_3_Monitors.add(new HttpMonitor("testmonitor8","test ip address2", 5, 80));
        user_3_Monitors.add(new HttpMonitor("testmonitor9","test ip address3", 5, 80));
        users[3].setRoles(Collections.singletonList(new UserRole(UserRole.UserRoleType.REGULAR)));

        users[4] = new User("admin5", "admin123");
        List <BaseMonitor> user_4_Monitors = new ArrayList<>();
        user_4_Monitors.add(new HttpMonitor("testmonitor1","test ip address1", 5, 80));
        user_4_Monitors.add(new HttpMonitor("testmonitor2","test ip address2", 5, 80));
        user_4_Monitors.add(new HttpMonitor("testmonitor3","test ip address3", 5, 80));
        users[4].setRoles(Collections.singletonList(new UserRole(UserRole.UserRoleType.REGULAR)));

        for (User user : users) {
            testEntityManager.persist(user);
        }
        testEntityManager.flush();
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

    @Test
    public void testExistsUserByUsername() {
        assert repository.existsUserByUsername("admin") == true;
//        assert repository.existsUserByUsername("wrongname") == false;
    }



//    @Test(expected = NullPointerException.class)
    public void testFindAllWithNullPageable() {
        // TODO: test specification find

    }

//    @Test
    public void testFindallWithPageable() {
        // TODO: test specification find
    }

//    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void testFindallInvalidSpec() throws InvalidDataException {
        // TODO: test specification find
    }

  //  @Test
    public void testFindAll() throws InvalidDataException {
        // TODO: test specification find

    }







    @After
    public void clean() {
        repository.deleteAll();
    }
}