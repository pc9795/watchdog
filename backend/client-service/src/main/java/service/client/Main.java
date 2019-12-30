package service.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.client.RestTemplate;
import service.client.utils.Constants;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Purpose: Entry point of the application
 **/
@EnableWebSecurity //Enable spring security
@EnableSwagger2 //Enable swagger
@Import(BeanValidatorPluginsConfiguration.class) //Swagger will detect bean validation annotations.
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
                .build().apiInfo(apiEndPointsInfo());
    }

    private ApiInfo apiEndPointsInfo() {
        return new ApiInfoBuilder().title("Watchdog REST API")
                .description("Session based authentication is used in this API")
                .contact(new Contact("Prashant Chaubey", "18200540", "prashant.chaubey@ucdconnect.ie"))
                .version("1.0.0")
                .build();
    }

    /**
     * Rest client to access other services.
     *
     * @param builder configuration object
     * @return rest template object
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
