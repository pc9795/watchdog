package service.client.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import service.client.entities.UserRole;
import service.client.service.ApiUserDetailsService;
import service.client.utils.Constants;
import service.client.utils.Utils;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Created By: Prashant Chaubey
 * Created On: 25-10-2019 19:33
 * Purpose: Spring security configuration.
 **/
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final DataSource dataSource;
    private final ApiUserDetailsService service;
    private final RestAuthenticationEntryPoint entryPoint;

    @Autowired
    public SecurityConfig(DataSource dataSource, ApiUserDetailsService service,
                          RestAuthenticationEntryPoint entryPoint) {
        this.dataSource = dataSource;
        this.service = service;
        this.entryPoint = entryPoint;
    }

    /**
     * Enabling jdbc authentication.
     *
     * @param auth
     * @throws Exception
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // We could have used 'defaultSchema' but it won't work with postgresql and mysql.
        auth.userDetailsService(service).passwordEncoder(encoder()).
                and().
                jdbcAuthentication().dataSource(dataSource);
    }

    /**
     * Configure http url access.
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().
                exceptionHandling().
                authenticationEntryPoint(entryPoint). // Custom handling on authentication failures
                accessDeniedHandler( // Custom handling of access denied.
                (request, response, accessDeniedException) -> {
                    Utils.createJSONErrorResponse(HttpServletResponse.SC_FORBIDDEN,
                            Constants.ErrorMsg.FORBIDDEN_RESOURCE, response);
                }).
                and().
                authorizeRequests(). // Authorization
                antMatchers(Constants.ApiV1Resource.USER + "/**").
                hasAnyRole("" + UserRole.UserRoleType.ADMIN, "" + UserRole.UserRoleType.REGULAR).
                and().
                logout().permitAll().
                logoutSuccessHandler(
                        ((request, response, authentication) -> new HttpStatusReturningLogoutSuccessHandler())
                );
//
//        http
//                .antMatcher(Constants.ApiV1Resource.USER);
    }

    /**
     * Password encoder to encode user passwords.
     *
     * @return
     */
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

}
