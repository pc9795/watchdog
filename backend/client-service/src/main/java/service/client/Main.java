package service.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import service.client.utils.Constants;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Purpose: Entry point of the application
 **/
@EnableWebSecurity //Enable spring security
@EnableSwagger2 //Enable swagger
@SpringBootApplication
@EntityScan(value = "core.entities.cockroachdb")
@EnableJpaRepositories(basePackages = {"core.repostiories.cockroachdb"})
@EnableMongoRepositories(basePackages = {"core.repostiories.mongodb"})
@PropertySource(value = "classpath:watchdog.properties")
public class Main {
    /**
     * Main method
     *
     * @param args program arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    /**
     * For swagger configuration
     *
     * @return configuration object
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(Constants.WATCHDOG_BASE_PACKAGE))
                .paths(PathSelectors.any())
                .build();
    }
}
