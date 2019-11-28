package service.client.api.v1;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;

@TestPropertySource(locations="classpath:test.properties")
public class SetUpForTest {
    @MockBean
    protected DataSource dataSource;

}
