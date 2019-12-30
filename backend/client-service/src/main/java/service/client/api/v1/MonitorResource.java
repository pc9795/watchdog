package service.client.api.v1;

import core.entities.cockroachdb.BaseMonitor;
import core.entities.cockroachdb.HttpMonitor;
import core.entities.cockroachdb.SocketMonitor;
import core.entities.cockroachdb.User;
import core.entities.mongodb.MonitorLog;
import core.repostiories.cockroachdb.MonitorRepository;
import core.repostiories.cockroachdb.UserRepository;
import core.repostiories.mongodb.MonitorLogRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import service.client.exceptions.BadDataException;
import service.client.exceptions.ForbiddenResourceException;
import service.client.exceptions.MonitoringServiceException;
import service.client.exceptions.ResourceNotFoundException;
import service.client.service.MonitoringServiceClient;
import service.client.utils.Constants;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

/**
 * Purpose: REST resource for accessing monitor.
 **/
@RestController
@RequestMapping(Constants.ApiV1Resource.MONITORS)
@Api(value = "Monitor resource", description = "Operations pertaining to manipulate monitors in Watchdog. Requests " +
        "should have a valid session cookie.")
public class MonitorResource {
    private static Logger LOGGER = LoggerFactory.getLogger(MonitorResource.class);
    private final MonitorRepository monitorRepository;
    private final UserRepository userRepository;
    private final MonitorLogRepository monitorLogRepository;
    private final MonitoringServiceClient monitoringServiceClient;

    @Autowired
    public MonitorResource(MonitorRepository monitorRepository, UserRepository userRepository,
                           MonitorLogRepository monitorLogRepository, MonitoringServiceClient monitoringServiceClient) {
        this.monitorRepository = monitorRepository;
        this.userRepository = userRepository;
        this.monitorLogRepository = monitorLogRepository;
        this.monitoringServiceClient = monitoringServiceClient;
    }


    /**
     * Get all the monitors for a user.
     *
     * @param principal logged in user
     * @return list of monitors
     */
    @GetMapping()
    @ApiOperation(value = "Get all monitors for the authenticated user")
    public List<BaseMonitor> getAllMonitors(Principal principal) {
        User user = userRepository.findUserByUsername(principal.getName());
        return monitorRepository.findAllByUser(user);
    }

    /**
     * Get a particular monitor for a user.
     *
     * @param monitorId db id of the monitor
     * @param principal logged in user
     * @return monitor
     * @throws ResourceNotFoundException  monitor with that id does not exist
     * @throws ForbiddenResourceException request for a monitor not created by the logged in user.
     */
    @GetMapping("/{monitor_id}")
    @ApiOperation(value = "Get the monitor with given database id if created by authenticated user")
    public BaseMonitor getMonitor(@PathVariable("monitor_id") long monitorId, Principal principal)
            throws ResourceNotFoundException, ForbiddenResourceException {
        return getUsersMonitor(monitorId, principal.getName());
    }

    /**
     * Create a monitor
     *
     * @param monitor   monitor object
     * @param principal logged in user
     * @return created monitor
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create a monitor")
    public BaseMonitor createMonitor(@Valid @RequestBody BaseMonitor monitor, Principal principal) {
        User user = userRepository.findUserByUsername(principal.getName());
        monitor.setUser(user); //Maintaining foreign key constraint
        return monitorRepository.save(monitor); //Save
    }

    /**
     * Edit already saved monitor
     *
     * @param monitorId db id of the monitor
     * @param monitor   new details for the monitor
     * @param principal logged in user
     * @return updated monitor
     * @throws ResourceNotFoundException  monitor with given id doesn't exist
     * @throws ForbiddenResourceException the monitor is not created by logged in user
     * @throws BadDataException           you can't send different type of child monitor. ex - can't send HTTPMonitor to
     *                                    edit a SocketMonitor object.
     */
    @PutMapping("/{monitor_id}")
    @ApiOperation(value = "Update a monitor with give database id if created by authenticated user")
    public BaseMonitor updateMonitor(@PathVariable("monitor_id") long monitorId, @Valid @RequestBody BaseMonitor monitor,
                                     Principal principal) throws ResourceNotFoundException, ForbiddenResourceException,
            BadDataException {
        BaseMonitor dbMonitor = getUsersMonitor(monitorId, principal.getName());
        //Different types
        if (!dbMonitor.getClass().equals(monitor.getClass())) {
            throw new BadDataException(String.format("Expected:  %s, got data of type: %s", dbMonitor.getClass().
                    getTypeName(), monitor.getClass().getTypeName()));
        }
        //Updating details
        dbMonitor.setName(monitor.getName());
        dbMonitor.setIpOrHost(monitor.getIpOrHost());
        dbMonitor.setMonitoringInterval(monitor.getMonitoringInterval());
        //Updating polymorphic fields
        if (monitor instanceof HttpMonitor) {
            ((HttpMonitor) dbMonitor).setExpectedStatusCode(((HttpMonitor) monitor).getExpectedStatusCode());
        } else if (monitor instanceof SocketMonitor) {
            ((SocketMonitor) dbMonitor).setSocketPort(((SocketMonitor) monitor).getSocketPort());
        }
        if (!monitoringServiceClient.editMonitor(monitorId, dbMonitor)) {
            LOGGER.error(String.format("Monitoring service is not able to edit monitor of id:%s", monitorId));
            throw new MonitoringServiceException("Could not able to edit the monitor right now!");
        }
        //Save
        return monitorRepository.save(dbMonitor);
    }

    /**
     * Delete the monitor with given id.
     *
     * @param monitorId db id of the monitor to be deleted
     * @param principal logged in user
     * @throws ResourceNotFoundException  When a monitor with given id doesn't exist in the database.
     * @throws ForbiddenResourceException When the given monitor is not created by the logged in user.
     */
    @DeleteMapping("/{monitor_id}")
    @ApiOperation(value = "Delete the monitor with given database id if created by authenticated user")
    public void deleteAUsersMonitor(@PathVariable("monitor_id") long monitorId, Principal principal)
            throws ResourceNotFoundException, ForbiddenResourceException {
        BaseMonitor dbMonitor = getUsersMonitor(monitorId, principal.getName());
        if (!monitoringServiceClient.deleteMonitor(monitorId)) {
            LOGGER.error(String.format("Monitoring service is not able to delete monitor of id:%s", monitorId));
            throw new MonitoringServiceException("Could not able to delete the monitor right now!");
        }
        //Delete
        monitorRepository.delete(dbMonitor);
    }

    /**
     * Get the status of the monitor. It will get the latest log related to the monitor.
     *
     * @param monitorId db id of the monitor.
     * @param principal logged in user
     * @return latest log entry of the monitor. It can also return null if monitoring is not started.
     */
    @GetMapping("/{monitor_id}/status")
    @ApiOperation(value = "Status of the monitor with give monitor id. If monitor id is not valid then returns null")
    public MonitorLog getMonitorStatus(@PathVariable("monitor_id") long monitorId, Principal principal) {
        return monitorLogRepository.findTopByMonitorIdAndUsernameOrderByCreationTimeDesc(monitorId,
                principal.getName());
    }

    /**
     * Get a configured no of logs for the monitor
     *
     * @param monitorId db id of the monitor
     * @param principal logged in user
     * @return latest logs for the monitor.
     */
    @GetMapping("/{monitor_id}/logs")
    @ApiOperation(value = "Logs of the monitor with given monitor id. If monitor id is not valid then returns null")
    public List<MonitorLog> getMonitorLogs(@PathVariable("monitor_id") long monitorId, Principal principal) {
        Pageable pageable = PageRequest.of(0, Constants.MAXIMUM_MONITORING_LOGS);
        return monitorLogRepository.findByMonitorIdAndUsernameOrderByCreationTimeDesc(pageable, monitorId,
                principal.getName());
    }

    /**
     * Utility method to extract a monitor when a user is logged in.
     *
     * @param monitorId db id of the monitor
     * @param username  username of the user currently logged in.
     * @return monitor object
     * @throws ResourceNotFoundException  if monitor with given id doesn't exist in the database.
     * @throws ForbiddenResourceException if monitor is not created by the user with the given username.
     */
    private BaseMonitor getUsersMonitor(long monitorId, String username) throws ResourceNotFoundException,
            ForbiddenResourceException {
        BaseMonitor monitor = monitorRepository.findById(monitorId);
        //Monitor doesn't exist.
        if (monitor == null) {
            throw new ResourceNotFoundException(String.format("Monitor id:%s", monitorId));
        }
        //Monitor is created by user
        if (!monitor.getUser().getUsername().equals(username)) {
            throw new ForbiddenResourceException();
        }
        return monitor;
    }
}
