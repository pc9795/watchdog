//package service.client.api.v1;
//
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import service.client.config.ApiUserPrincipal;
//import service.client.entities.User;
//
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Primary;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import service.client.entities.UserRole;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//@TestConfiguration
//public class SetUpForTest {
//
//    @Bean
//    @Primary
//    public UserDetailsService userDetailsService() {
//        List<UserRole> theListOfUsersRoles = new ArrayList<UserRole>();
//        theListOfUsersRoles.add(new UserRole(UserRole.UserRoleType.REGULAR));
//
//        User basicUser = (new User("Ferdia", "password"));
//        basicUser.setRoles(theListOfUsersRoles);
//        ApiUserPrincipal basicActiveUser = new ApiUserPrincipal(basicUser);
//
//        User managerUser = new UserImpl("Manager User", "manager@company.com", "password");
//        UserActive managerActiveUser = new UserActive(managerUser, Arrays.asList(
//                new SimpleGrantedAuthority("ROLE_MANAGER"),
//                new SimpleGrantedAuthority("PERM_FOO_READ"),
//                new SimpleGrantedAuthority("PERM_FOO_WRITE"),
//                new SimpleGrantedAuthority("PERM_FOO_MANAGE")
//        ));
//
//        return new InMemoryUserDetailsManager(Arrays.asList(
//                basicActiveUser, managerActiveUser
//        ));
//    }
//}
