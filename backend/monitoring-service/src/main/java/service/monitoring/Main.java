package service.monitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Purpose: Entry point of the application.
 **/
@PropertySource(value = "classpath:watchdog.properties")
@SpringBootApplication(scanBasePackages = {"core", "service.monitoring"})
@EntityScan(value = "core.entities.cockroachdb")
@EnableJpaRepositories(basePackages = {"core.repostiories.cockroachdb"})
@EnableMongoRepositories(basePackages = {"core.repostiories.mongodb"})
public class Main {
    /**
     * Main method
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
