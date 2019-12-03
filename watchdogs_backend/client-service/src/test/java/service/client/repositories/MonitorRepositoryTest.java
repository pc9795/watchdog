package service.client.repositories;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import service.client.entities.BaseMonitor;
import service.client.entities.HttpMonitor;
import service.client.entities.User;
import service.client.entities.UserRole;
import service.client.exceptions.InvalidDataException;
import service.client.utils.ApiSpecification;
import service.client.utils.SearchCriteria;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MonitorRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private MonitorRepository monitorRepository;

    private User testUser;
    private BaseMonitor[] monitors;

    @Before
    public void setup() {
        testUser = new User("admin", "admin123");
        testUser.setRoles(Collections.singletonList(new UserRole(UserRole.UserRoleType.REGULAR)));

        monitors = new BaseMonitor[5];

        monitors[0] = new HttpMonitor("testmonitor1","test ip address1", 5, 80);
        monitors[1] = new HttpMonitor("testmonitor2","test ip address2", 5, 80);
        monitors[2] = new HttpMonitor("testmonitor3","test ip address3", 5, 80);
        monitors[3] = new HttpMonitor("testmonitor4","test ip address4", 5, 80);
        monitors[4] = new HttpMonitor("testmonitor5","test ip address5", 5, 80);


//        baseMonitor[0].setCalories(100);
//        baseMonitor[0].setText("food1");
//        baseMonitor[0].setLessThanExpected(true);
//        baseMonitor[0].setTime(LocalTime.MIDNIGHT);
//        baseMonitor[0].setDate(LocalDate.of(2019, 10, 27));

//        meals[1] = new Meal();
//        meals[1].setCalories(200);
//        meals[1].setText("food2");
//        meals[1].setLessThanExpected(false);
//        meals[1].setTime(LocalTime.NOON);
//        meals[1].setDate(LocalDate.of(2019, 10, 28));
//
//        meals[2] = new Meal();
//        meals[2].setCalories(300);
//        meals[2].setText("food3");
//        meals[2].setLessThanExpected(true);
//        meals[2].setTime(LocalTime.MIDNIGHT);
//        meals[2].setDate(LocalDate.of(2019, 10, 29));
//
//        meals[3] = new Meal();
//        meals[3].setCalories(400);
//        meals[3].setText("food4");
//        meals[3].setLessThanExpected(false);
//        meals[3].setTime(LocalTime.NOON);
//        meals[3].setDate(LocalDate.of(2019, 10, 30));
//
//        meals[4] = new Meal();
//        meals[4].setCalories(500);
//        meals[4].setText("food5");
//        meals[4].setLessThanExpected(true);
//        meals[4].setTime(LocalTime.MIDNIGHT);
//        meals[4].setDate(LocalDate.of(2019, 11, 1));

        // Will give it id of 1.
        for (BaseMonitor aMonitor : monitors) {
            testUser.addMonitor(aMonitor);
        }
        // Meals will have ids of 2, 3, 4, 5 respectively.
        testEntityManager.persist(testUser);
        for (BaseMonitor aMonitor : monitors) {
            testEntityManager.persist(aMonitor);
        }
        testEntityManager.flush();
    }


    @Test
    public void createMonitor() {
        BaseMonitor createdMonitor = monitorRepository.createMonitor(
                testUser,
                new HttpMonitor("testCreateMonitor","testCreateMonitor address1", 5, 80)
        );

        assert (createdMonitor.getUser().getId() == testUser.getId());

    }

    @Test
    public void findById() {
        //BaseMonitor findById(long id);
        assert monitorRepository.findById(2).getId() == 2;
        assert monitorRepository.findById(-1) == null;
    }

    @Test
    public void existsBaseMonitorById() {
        //boolean existsBaseMonitorById(long id);
        assert monitorRepository.existsBaseMonitorById(2L) == true;
        assert monitorRepository.existsBaseMonitorById(7) == false;
    }

    @Test
    public void findAll() {
        //boolean existsBaseMonitorById(long id);
        assert (monitorRepository.findAll().equals(Arrays.asList(monitors)));
    }






}