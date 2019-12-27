package service.client.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import service.client.service.ApiUserDetailsService;
import service.client.utils.Constants;
import service.client.utils.Utils;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Collections;

/**
 * Purpose: Spring security configuration.
 **/
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final DataSource dataSource;
    private final ApiUserDetailsService service;

    //Injected by spring
    @Value("${cors_url}")
    private String corsURL;

    @Autowired
    public SecurityConfig(DataSource dataSource, ApiUserDetailsService service) {
        this.dataSource = dataSource;
        this.service = service;
    }

    /**
     * Enabling jdbc authentication.
     *
     * @param auth configuration object
     * @throws Exception if something goes wrong
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // We could have used 'defaultSchema' but it won't work with postgresql.
        auth.userDetailsService(service).passwordEncoder(encoder()).
                and().
                jdbcAuthentication().dataSource(dataSource);
    }

    /**
     * Configure http url access.
     *
     * @param http configuration object
     * @throws Exception if something goes wrong
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //Enable CORS and disable CSRF
        http.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(
                (httpServletRequest, httpServletResponse, e) ->
                        // Custom handling on authentication failures
                        Utils.createJSONErrorResponse(HttpServletResponse.SC_UNAUTHORIZED, Constants.ErrorMsg.UNAUTHORIZED,
                                httpServletResponse)
        ).accessDeniedHandler(
                // Custom handling of access denied.
                (request, response, accessDeniedException) -> {
                    Utils.createJSONErrorResponse(HttpServletResponse.SC_FORBIDDEN,
                            Constants.ErrorMsg.FORBIDDEN_RESOURCE, response);
                })
                .and()
                .authorizeRequests()
                //Authenticate all request for monitors
                .antMatchers(Constants.ApiV1Resource.MONITORS + "/**").authenticated()
                .and()
                .logout().permitAll()
                .logoutSuccessHandler(
                        //Add logout functionality given by spring security
                        ((request, response, authentication) -> new HttpStatusReturningLogoutSuccessHandler())
                );

    }

    /**
     * Password encoder to encode user passwords.
     *
     * @return password encoder
     */
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuration to enable CORS
     *
     * @return cors configuration object
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(Collections.singletonList(corsURL));
        configuration.setAllowedMethods(Arrays.asList("POST", "PUT", "GET", "OPTIONS", "DELETE", "PATCH"));
        configuration.setMaxAge(3600L);
        configuration.setAllowedHeaders(Arrays.asList("X-Requested-With", "WWW-Authenticate", "Authorization", "Origin",
                "Content-Type", "Version"));
        configuration.setExposedHeaders(Arrays.asList("X-Requested-With", "WWW-Authenticate",
                "Authorization", "Origin", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
