package service.monitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * Created By: Prashant Chaubey
 * Created On: 06-12-2019 00:20
 * Purpose: Entry point of the application.
 **/
@PropertySource(value = "classpath:watchdog.properties")
@SpringBootApplication(scanBasePackages = {"core","service.monitoring"})
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
