package service.client.api.v1;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import service.client.config.ApiUserPrincipal;
import service.client.entities.*;
import service.client.exceptions.*;
import service.client.repositories.MonitorRepository;
import service.client.repositories.UserRepository;
import service.client.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;


import javax.persistence.DiscriminatorValue;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.security.Principal;
import java.util.List;

/**
 * Created By: Prashant Chaubey
 * Created On: 22-11-2019 00:32
 * Purpose: TODO:
 **/
@RestController
@RequestMapping(Constants.ApiV1Resource.USER)
public class UserResource {

    private final UserRepository userRepository;
    private final MonitorRepository monitorRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserResource(MonitorRepository monitorRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.monitorRepository = monitorRepository;
        this.userRepository = userRepository;

        this.passwordEncoder = passwordEncoder;

//        this.userRepository.save(new User("ferdia", "passssssss"));
//        this.userRepository.save(new User("dummy", "passssssss"));
    }

    // START USER requests:

    @PostMapping("/create_user")
    public User addUser(@PathParam("user") @Valid User user, Principal principal) throws UserAlreadyExistException, InvalidDataException {
        //Non admin can't add admin account.
        if (!Utils.isPrincipalAdmin(principal) && user.isAdmin()) {
            throw new InvalidDataException("Non admin user can't add an admin user");
        }
        // Check if user with username already exists
        throwException_IfUsernameAlreadyExist(passwordEncoder.encode(user.getUsername()));
        // Create user
        return this.userRepository.save(user);
    }

    @GetMapping("/{user_id}")
    public User getUserById(@PathVariable("user_id") long id) throws UserDoesntExist {
        throwException_IfUserIdDoesntExist(id);

        return this.userRepository.findUserById(id);
    }

//    @GetMapping("/{username}/{password}")
//    public User getUserByUsername(@PathVariable("username") String username,@PathVariable("password") String password) throws UserDoesntExist, UserWithUsernameDoesNotExist, UsernamePasswordIncorrect {
//        User theUser = userRepository.findUserByUsername(username);
//        if(theUser != null){
//            throw new UserWithUsernameDoesNotExist(username);
//        }
//        if(!theUser.getPassword().equals(password)){
//            throw new UsernamePasswordIncorrect();
//        }
//        // else the password is correct
//        return theUser;
//    }


    @PutMapping("/{user_id}/updateUser")
    public void updateUser(@PathVariable("user_id") long userId, @PathParam("user") User userUpdate, Principal principal) throws UserDoesntExist, UserAlreadyExistException, InvalidDataException {
        //todo: this allows user to update username and password, mite need more
        throwException_IfUserIdDoesntExist(userId);     // check if user with id exists

        if (!Utils.isPrincipalAdmin(principal) && userUpdate.isAdmin()) {
            throw new InvalidDataException("Non admin user can't update to an admin user");
        }
        userUpdate.setPassword(passwordEncoder.encode(userUpdate.getPassword()));   // encode  updated password

        User userToUpdate = this.userRepository.findUserById(userId);

        String theOldUsername =  userToUpdate.getUsername();
        // Check if newName is same as old name
        if(!userToUpdate.equals(userUpdate.getUsername())){
            throw new UserAlreadyExistException();
        }
        // check if user aleady exists
        throwException_IfUsernameAlreadyExist(userUpdate.getUsername());

        // then update the user
        userToUpdate.setUsername(userUpdate.getUsername());
        userToUpdate.setPassword(userUpdate.getPassword());
        userToUpdate.setRoles(userUpdate.getRoles());

        this.userRepository.save(userToUpdate);
        // We are changed details of the logged in user re-authenticate.
        if (userToUpdate.equals(principal.getName())) {
            ApiUserPrincipal newPrincipal = new ApiUserPrincipal(userToUpdate);
            Authentication authentication = new UsernamePasswordAuthenticationToken(newPrincipal,
                    newPrincipal.getPassword(), newPrincipal.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    @DeleteMapping("/{user_id}/deleteUser")
    public void deleteUser(@PathVariable("user_id") long userId, Principal principal, HttpServletRequest request) throws UserDoesntExist, InvalidDataException {
        throwException_IfUserIdDoesntExist(userId);
        User theUserToDelete = userRepository.findUserById(userId);
        // then delete user
        //Non admin can't update to admin role.
        if (!Utils.isPrincipalAdmin(principal) && theUserToDelete.isAdmin()) {
            throw new InvalidDataException("Non admin user can't delete an admin user");
        }

        //We are deleting the user which is logged in.
        if (theUserToDelete.getUsername().equals(principal.getName())) {
            HttpSession session = request.getSession(false);
            SecurityContextHolder.clearContext();
            if (session != null) {
                session.invalidate();
            }
        }
        userRepository.delete(theUserToDelete);
    }

    // user monitor requests:
    @GetMapping("/{user_id}/getMonitors")
    public List<BaseMonitor> getUserMonitors(Pageable pageable, @RequestParam(value = "search", required = false) String search, @PathVariable("user_id") long userId, Principal principal) throws UserDoesntExist, ForbiddenResourceException, InvalidDataException, ResourceNotExistException {
        boolean isAdmin = Utils.isPrincipalAdmin(principal);
        //Non admin can't view all.
        if (!isAdmin) {
            throw new ForbiddenResourceException();
        }

        Specification<BaseMonitor> spec = search == null ? null : SpecificationUtils.getSpecFromQuery(search, SpecificationUtils::mealAttributeConverter);
        throwException_IfUserIdDoesntExist(userId);     // check if user with id exists
        User theUser = userRepository.findUserById(userId);
        if (!isAdmin && !principal.getName().equals(theUser.getUsername())) {
            throw new ForbiddenResourceException();
        }
        //Add the user to the specification.
        Specification<BaseMonitor> userSpec = new ApiSpecification<>(new SearchCriteria("user", theUser, "eq"));
        spec = spec == null ? userSpec : spec.and(userSpec);
        return monitorRepository.findAll(spec, pageable).getContent();
    }

    @GetMapping("/{user_id}/{monitor_id}")
    public BaseMonitor getUserMonitorFromId(@PathVariable("user_id") long userId, @PathVariable("monitor_id") long monitorId, Principal principal) throws UserDoesntExist, MonitorDoesExistOrDoesNotBelongToUser {
        if(monitorRepository.existsById(monitorId)) {
            BaseMonitor usersMonitor = (BaseMonitor) monitorRepository.findById(monitorId);
            if (Utils.isPrincipalAdmin(principal) && usersMonitor.getUser().getId() == userId) {
                return usersMonitor;
            }
        }
        // Else throw exception
        throw new MonitorDoesExistOrDoesNotBelongToUser(userId,monitorId);
    }

    // END USER requests;

    // START MONITOR requests:
    // Adding monitor Methods:

    @PostMapping("/{user_id}/addMonitor/http")
    public HttpMonitor addHttpMonitorToUser(@PathVariable("user_id") long userId, @PathParam("http_monitor") @Valid HttpMonitor httpMonitor, Principal principal) throws UserDoesntExist, ForbiddenResourceException {
        System.out.println("the type is " + httpMonitor.getClass().getAnnotation(DiscriminatorValue.class).value().toString());
        return (HttpMonitor) addMonitorToUser(userId,httpMonitor, principal);
    }

    @PostMapping("/{user_id}/addMonitor/socket")
    public SocketMonitor addSocketMonitorToUser(@PathVariable("user_id") long userId, @PathParam("socket_monitor") @Valid SocketMonitor socketMonitor, Principal principal) throws UserDoesntExist, ForbiddenResourceException {
        return (SocketMonitor) addMonitorToUser(userId,socketMonitor, principal);

    }

    @PostMapping("/{user_id}/addMonitor/ping")
    public PingMonitor addPingMonitorToUser(@PathVariable("user_id") long userId, @PathParam("ping_monitor") @Valid PingMonitor pingMonitor, Principal principal) throws UserDoesntExist, ForbiddenResourceException {
        return (PingMonitor) addMonitorToUser(userId,pingMonitor, principal);
    }

    private BaseMonitor addMonitorToUser(long userId, BaseMonitor monitor, Principal principal) throws UserDoesntExist, ForbiddenResourceException {
        if(!userRepository.existsById(userId)){
            throw new UserDoesntExist(userId);
        }
        // Then no error, so add the monitor
        User userWithId = userRepository.findUserById(userId);
        if(!userWithId.getUsername().equals(principal.getName())){
            throw new ForbiddenResourceException();
        }
        BaseMonitor theNewMonitor = monitorRepository.createMonitor(userWithId,monitor);
        userWithId.addMonitor(theNewMonitor);
        return (BaseMonitor) theNewMonitor;
    }

    // end of adding monitor methods

    // updateing monitor methods
    @PutMapping("/{user_id}/updateHttpMonitor/{monitor_id}")
    public void updateAUsersHttpMonitor(@PathVariable("user_id") long userId, @PathVariable("monitor_id") long monitorId, @PathParam("http_monitor") HttpMonitor updatedHttpMonitor, Principal principal)
            throws UserDoesntExist, UserAlreadyExistException, MonitorDoesExistOrDoesNotBelongToUser {
        HttpMonitor monitorToUpdate = (HttpMonitor)updateAUsersMonitor(userId, monitorId, updatedHttpMonitor,principal);
        monitorToUpdate.setExpectedHttpStatusCode(updatedHttpMonitor.getExpectedHttpStatusCode());
        this.monitorRepository.save(monitorToUpdate);
    }

    @PutMapping("/{user_id}/updateSocketMonitor/{monitor_id}")
    public void updateAUsersSocketMonitor(@PathVariable("user_id") long userId, @PathVariable("monitor_id") long monitorId, @PathParam("socket_monitor") SocketMonitor socketMonitor, Principal principal)
            throws UserDoesntExist, UserAlreadyExistException, MonitorDoesExistOrDoesNotBelongToUser {
        SocketMonitor monitorToUpdate = (SocketMonitor)updateAUsersMonitor(userId, monitorId, socketMonitor,principal);
        monitorToUpdate.setSocketPort(socketMonitor.getSocketPort());
        this.monitorRepository.save(monitorToUpdate);
    }

    @PutMapping("/{user_id}/updatePingMonitor/{monitor_id}")
    public void updateAUsersPingMonitor(@PathVariable("user_id") long userId, @PathVariable("monitor_id") long monitorId, @PathParam("ping_monitor") PingMonitor pingMonitor, Principal principal)
            throws UserDoesntExist, UserAlreadyExistException, MonitorDoesExistOrDoesNotBelongToUser {
        this.monitorRepository.save(updateAUsersMonitor(userId, monitorId, pingMonitor,principal));
    }

    private BaseMonitor updateAUsersMonitor(long userId, long monitorId, BaseMonitor updatedMonitor, Principal principal) throws MonitorDoesExistOrDoesNotBelongToUser {
        if(monitorRepository.existsById(monitorId)){
            HttpMonitor monitorToUpdate = (HttpMonitor)monitorRepository.findById(monitorId);
            User theUserWhoOwnsMonitor = monitorToUpdate.getUser();
            if(principal.getName().equals(theUserWhoOwnsMonitor.getUsername()) && theUserWhoOwnsMonitor.getId() == userId){
                monitorToUpdate.setName(updatedMonitor.getName());
                monitorToUpdate.setIpOrUrlOrHost(updatedMonitor.getIpOrUrlOrHost());
                monitorToUpdate.setMonitoringInterval(updatedMonitor.getMonitoringInterval());
                return monitorToUpdate;
            }
        }
        // Else throw exception
        throw new MonitorDoesExistOrDoesNotBelongToUser(userId,monitorId);
    }

    // deleteing monitor methods
    @DeleteMapping("/{user_id}/deleteMonitor/{monitor_id}")
    public void deleteAUsersMonitor(@PathVariable("user_id") long userId, @PathVariable("monitor_id") long monitorId, Principal principal) throws MonitorDoesExistOrDoesNotBelongToUser {
        if(monitorRepository.existsById(monitorId)){
            BaseMonitor theMonitorToDelete = monitorRepository.findById(monitorId);
            User theUserWhoOwnsMonitor = theMonitorToDelete.getUser();
            if(principal.getName().equals(theUserWhoOwnsMonitor.getUsername()) && theUserWhoOwnsMonitor.getId() == userId){
                monitorRepository.delete(theMonitorToDelete);
                return;
            }
        }
        // Else throw exception
        throw new MonitorDoesExistOrDoesNotBelongToUser(userId,monitorId);
    }
    
    // end of deleting monitor methods
    // END MONITOR requests;

    private void throwException_IfUsernameAlreadyExist(String username) throws UserAlreadyExistException {
        if(userRepository.existsUserByUsername(username)){
            System.out.println("The username already exists");
            throw new UserAlreadyExistException(username);
        }
    }

    private void throwException_IfUserIdDoesntExist(Long id) throws UserDoesntExist {
        if(!userRepository.existsById(id)){
            //then user exists already
            throw new UserDoesntExist(id);
        }
    }


}
