package service.client.api.v1;

import org.springframework.data.domain.Pageable;
import service.client.entities.*;
import service.client.exceptions.ForbiddenResourceException;
import service.client.exceptions.UserAlreadyExistException;
import service.client.repositories.MonitorRepository;
import service.client.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import service.client.utils.Utils;

import javax.persistence.DiscriminatorValue;
import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

/**
 * Created By: Prashant Chaubey
 * Created On: 22-11-2019 00:33
 * Purpose: TODO:
 **/
@RestController
@RequestMapping(Constants.ApiV1Resource.MONITORS)
public class MonitorResource {

    private final MonitorRepository monitorRepository;

    @Autowired
    public MonitorResource(MonitorRepository monitorRepository) {
        this.monitorRepository = monitorRepository;
    }

    public Optional<BaseMonitor> getBaseMonitorById(Long baseMonitorId) throws ResourceNotExistException {
        if(monitorRepository.existsBaseMonitorById(baseMonitorId)){
            throw new ResourceNotExistException("monitor with " + baseMonitorId + " Does not exists");
        }
        return monitorRepository.findById(baseMonitorId);
    }


    @GetMapping("/getAll")
    public List<BaseMonitor> getAllMonitors() {
        return monitorRepository.findAll();
    }

    @GetMapping("/getMonitor/{monitor_id}")
    public BaseMonitor getMonitorById(@PathVariable("monitor_id") long monitorId) throws ResourceNotExistException {
        if(!monitorRepository.existsBaseMonitorById(monitorId)){
            throw new ResourceNotExistException("monitor with id does not exist: " + monitorId);
        }
        return monitorRepository.findById(monitorId);
    }

    @PutMapping("/{monitor_id}")
    public void updateMonitor(@PathParam("monitor_id") long id) {
        //todo implement
    }

    @GetMapping("/{monitor_id}")
    public void getMonitorStatus(@PathParam("monitor_id") long id) {
        //todo implement
    }

    // user monitor requests:
    @GetMapping("/{user_id}/getMonitors")
    public List<BaseMonitor> getUserMonitors(Pageable pageable, @RequestParam(value = "search", required = false) String search, @PathVariable("user_id") long userId, Principal principal) throws UserDoesntExist, ForbiddenResourceException, InvalidDataException, ResourceNotExistException {
        boolean isAdmin = Utils.isPrincipalAdmin(principal);
        //Non admin can't view all.
        if (!isAdmin) {
            throw new ForbiddenResourceException();
        }

//        //Specification<BaseMonitor> spec = search == null ? null : SpecificationUtils.getSpecFromQuery(search, SpecificationUtils::mealAttributeConverter);
//        throwException_IfUserIdDoesntExist(userId);     // check if user with id exists
//        User theUser = userRepository.findById(userId);
//        if (!isAdmin && !principal.getName().equals(theUser.getUsername())) {
//            throw new ForbiddenResourceException();
//        }
//        //Add the user to the specification.
//        Specification<BaseMonitor> userSpec = new ApiSpecification<>(new SearchCriteria("user", theUser, "eq"));
//        spec = spec == null ? userSpec : spec.and(userSpec);
//        return monitorRepository.findAll(spec, pageable).getContent();

        throwException_IfUserIdDoesntExist(userId);     // check if user with id exists
        User theUser = userRepository.findById(userId);
        if (!isAdmin && !principal.getName().equals(theUser.getUsername())) {
            throw new ForbiddenResourceException();
        }
        //Add the user to the specification.
        return theUser.getMonitors();
    }

    @GetMapping("/{user_id}/{monitor_id}")
    public BaseMonitor getUserMonitorFromId(@PathVariable("user_id") long userId, @PathVariable("monitor_id") long monitorId, Principal principal) throws UserDoesntExist, MonitorDoesExistOrDoesNotBelongToUser {
        if (monitorRepository.existsById(monitorId)) {
            BaseMonitor usersMonitor = (BaseMonitor) monitorRepository.findById(monitorId);
            if (Utils.isPrincipalAdmin(principal) && usersMonitor.getUser().getId() == userId) {
                return usersMonitor;
            }
        }
        // Else throw exception
        throw new MonitorDoesExistOrDoesNotBelongToUser(userId, monitorId);
    }

    // END USER requests;

    // START MONITOR requests:
    // Adding monitor Methods:

    @PostMapping("/{user_id}/addMonitor/http")
    public HttpMonitor addHttpMonitorToUser(@PathVariable("user_id") long userId, @PathParam("http_monitor") @Valid HttpMonitor httpMonitor, Principal principal) throws UserDoesntExist, ForbiddenResourceException {
        System.out.println("the type is " + httpMonitor.getClass().getAnnotation(DiscriminatorValue.class).value().toString());
        return (HttpMonitor) addMonitorToUser(userId, httpMonitor, principal);
    }

    @PostMapping("/{user_id}/addMonitor/socket")
    public SocketMonitor addSocketMonitorToUser(@PathVariable("user_id") long userId, @PathParam("socket_monitor") @Valid SocketMonitor socketMonitor, Principal principal) throws UserDoesntExist, ForbiddenResourceException {
        return (SocketMonitor) addMonitorToUser(userId, socketMonitor, principal);

    }

    @PostMapping("/{user_id}/addMonitor/ping")
    public PingMonitor addPingMonitorToUser(@PathVariable("user_id") long userId, @PathParam("ping_monitor") @Valid PingMonitor pingMonitor, Principal principal) throws UserDoesntExist, ForbiddenResourceException {
        return (PingMonitor) addMonitorToUser(userId, pingMonitor, principal);
    }

    private BaseMonitor addMonitorToUser(long userId, BaseMonitor monitor, Principal principal) throws UserDoesntExist, ForbiddenResourceException {
        if (!userRepository.existsById(userId)) {
            throw new UserDoesntExist(userId);
        }
        // Then no error, so add the monitor
        User userWithId = userRepository.findById(userId);
        if (!userWithId.getUsername().equals(principal.getName())) {
            throw new ForbiddenResourceException();
        }
        BaseMonitor theNewMonitor = monitorRepository.createMonitor(userWithId, monitor);
        userWithId.addMonitor(theNewMonitor);
        return (BaseMonitor) theNewMonitor;
    }

    // end of adding monitor methods

    // updateing monitor methods
    @PutMapping("/{user_id}/updateHttpMonitor/{monitor_id}")
    public void updateAUsersHttpMonitor(@PathVariable("user_id") long userId, @PathVariable("monitor_id") long monitorId, @PathParam("http_monitor") HttpMonitor updatedHttpMonitor, Principal principal)
            throws UserDoesntExist, UserAlreadyExistException, MonitorDoesExistOrDoesNotBelongToUser {
        HttpMonitor monitorToUpdate = (HttpMonitor) updateAUsersMonitor(userId, monitorId, updatedHttpMonitor, principal);
        monitorToUpdate.setExpectedHttpStatusCode(updatedHttpMonitor.getExpectedHttpStatusCode());
        this.monitorRepository.save(monitorToUpdate);
    }

    @PutMapping("/{user_id}/updateSocketMonitor/{monitor_id}")
    public void updateAUsersSocketMonitor(@PathVariable("user_id") long userId, @PathVariable("monitor_id") long monitorId, @PathParam("socket_monitor") SocketMonitor socketMonitor, Principal principal)
            throws UserDoesntExist, UserAlreadyExistException, MonitorDoesExistOrDoesNotBelongToUser {
        SocketMonitor monitorToUpdate = (SocketMonitor) updateAUsersMonitor(userId, monitorId, socketMonitor, principal);
        monitorToUpdate.setSocketPort(socketMonitor.getSocketPort());
        this.monitorRepository.save(monitorToUpdate);
    }

    @PutMapping("/{user_id}/updatePingMonitor/{monitor_id}")
    public void updateAUsersPingMonitor(@PathVariable("user_id") long userId, @PathVariable("monitor_id") long monitorId, @PathParam("ping_monitor") PingMonitor pingMonitor, Principal principal)
            throws UserDoesntExist, UserAlreadyExistException, MonitorDoesExistOrDoesNotBelongToUser {
        this.monitorRepository.save(updateAUsersMonitor(userId, monitorId, pingMonitor, principal));
    }

    private BaseMonitor updateAUsersMonitor(long userId, long monitorId, BaseMonitor updatedMonitor, Principal principal) throws MonitorDoesExistOrDoesNotBelongToUser {
        if (monitorRepository.existsById(monitorId)) {
            HttpMonitor monitorToUpdate = (HttpMonitor) monitorRepository.findById(monitorId);
            User theUserWhoOwnsMonitor = monitorToUpdate.getUser();
            if (principal.getName().equals(theUserWhoOwnsMonitor.getUsername()) && theUserWhoOwnsMonitor.getId() == userId) {
                monitorToUpdate.setName(updatedMonitor.getName());
                monitorToUpdate.setIpOrUrlOrHost(updatedMonitor.getIpOrUrlOrHost());
                monitorToUpdate.setMonitoringInterval(updatedMonitor.getMonitoringInterval());
                return monitorToUpdate;
            }
        }
        // Else throw exception
        throw new MonitorDoesExistOrDoesNotBelongToUser(userId, monitorId);
    }

    // deleteing monitor methods
    @DeleteMapping("/{user_id}/deleteMonitor/{monitor_id}")
    public void deleteAUsersMonitor(@PathVariable("user_id") long userId, @PathVariable("monitor_id") long monitorId, Principal principal) throws MonitorDoesExistOrDoesNotBelongToUser {
        if (monitorRepository.existsById(monitorId)) {
            BaseMonitor theMonitorToDelete = monitorRepository.findById(monitorId);
            User theUserWhoOwnsMonitor = theMonitorToDelete.getUser();
            if (principal.getName().equals(theUserWhoOwnsMonitor.getUsername()) && theUserWhoOwnsMonitor.getId() == userId) {
                monitorRepository.delete(theMonitorToDelete);
                return;
            }
        }
        // Else throw exception
        throw new MonitorDoesExistOrDoesNotBelongToUser(userId, monitorId);
    }
}
