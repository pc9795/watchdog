package service.client.api.v1;

import core.entities.cockroachdb.BaseMonitor;
import core.entities.cockroachdb.HttpMonitor;
import core.entities.cockroachdb.SocketMonitor;
import core.entities.cockroachdb.User;
import core.entities.mongodb.MonitorLog;
import core.repostiories.cockroachdb.MonitorRepository;
import core.repostiories.cockroachdb.UserRepository;
import core.repostiories.mongodb.MonitorLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import service.client.exceptions.BadDataException;
import service.client.exceptions.ForbiddenResourceException;
import service.client.exceptions.ResourceNotFoundException;
import service.client.utils.Constants;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

/**
 * Created By: Prashant Chaubey
 * Created On: 22-11-2019 00:33
 * Purpose: REST resource for accessing monitor.
 **/
@RestController
@RequestMapping(Constants.ApiV1Resource.MONITORS)
public class MonitorResource {

    private final MonitorRepository monitorRepository;
    private final UserRepository userRepository;
    private final MonitorLogRepository monitorLogRepository;

    @Autowired
    public MonitorResource(MonitorRepository monitorRepository, UserRepository userRepository,
                           MonitorLogRepository monitorLogRepository) {
        this.monitorRepository = monitorRepository;
        this.userRepository = userRepository;
        this.monitorLogRepository = monitorLogRepository;
    }


    @GetMapping()
    public List<BaseMonitor> getAllMonitors(Principal principal) {
        User user = userRepository.findUserByUsername(principal.getName());
        return monitorRepository.findAllByUser(user);
    }

    @GetMapping("/{monitor_id}")
    public BaseMonitor getMonitor(@PathVariable("monitor_id") long monitorId, Principal principal)
            throws ResourceNotFoundException, ForbiddenResourceException {
        return getUsersMonitor(monitorId, principal.getName());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BaseMonitor createMonitor(@Valid @RequestBody BaseMonitor monitor, Principal principal) throws BadDataException {
        if (monitor.getMonitoringInterval() < Constants.MINIMUM_MONITORING_INTERVAL) {
            throw new BadDataException(String.format("Minimum interval allowed:%s sec",
                    Constants.MINIMUM_MONITORING_INTERVAL));
        }
        User user = userRepository.findUserByUsername(principal.getName());
        monitor.setUser(user);
        //Save
        return monitorRepository.save(monitor);
    }

    @PutMapping("/{monitor_id}")
    public BaseMonitor updateMonitor(@PathVariable("monitor_id") long monitorId, @Valid @RequestBody BaseMonitor monitor,
                                     Principal principal) throws ResourceNotFoundException, ForbiddenResourceException,
            BadDataException {
        if (monitor.getMonitoringInterval() < Constants.MINIMUM_MONITORING_INTERVAL) {
            throw new BadDataException(String.format("Minimum interval allowed:%s sec",
                    Constants.MINIMUM_MONITORING_INTERVAL));
        }
        BaseMonitor dbMonitor = getUsersMonitor(monitorId, principal.getName());
        //Different types
        if (!dbMonitor.getClass().equals(monitor.getClass())) {
            throw new BadDataException(String.format("Expected:  %s, got data of type: %s", dbMonitor.getClass().
                    getTypeName(), monitor.getClass().getTypeName()));
        }
        dbMonitor.setName(monitor.getName());
        dbMonitor.setIpOrHost(monitor.getIpOrHost());
        dbMonitor.setMonitoringInterval(monitor.getMonitoringInterval());
        //Updating polymorphic fields
        if (monitor instanceof HttpMonitor) {
            ((HttpMonitor) dbMonitor).setExpectedStatusCode(((HttpMonitor) monitor).getExpectedStatusCode());
        } else if (monitor instanceof SocketMonitor) {
            ((SocketMonitor) dbMonitor).setSocketPort(((SocketMonitor) monitor).getSocketPort());
        }
        return monitorRepository.save(dbMonitor);
    }

    @DeleteMapping("/{monitor_id}")
    public void deleteAUsersMonitor(@PathVariable("monitor_id") long monitorId, Principal principal)
            throws ResourceNotFoundException, ForbiddenResourceException {
        BaseMonitor dbMonitor = getUsersMonitor(monitorId, principal.getName());
        //Delete
        monitorRepository.delete(dbMonitor);
    }

    @GetMapping("/{monitor_id}/status")
    public MonitorLog getMonitorStatus(@PathVariable("monitor_id") long monitorId, Principal principal) {
        return monitorLogRepository.findTopByMonitorIdAndUsernameOrderByCreationTimeDesc(monitorId,
                principal.getName());
    }

    @GetMapping("/{monitor_id}/logs")
    public List<MonitorLog> getMonitorLogs(@PathVariable("monitor_id") long monitorId, Principal principal) {
        Pageable pageable = PageRequest.of(0, Constants.MAXIMUM_MONITORING_LOGS);
        return monitorLogRepository.findByMonitorIdAndUsernameOrderByCreationTimeDesc(pageable, monitorId,
                principal.getName());
    }

    //Helper method
    private BaseMonitor getUsersMonitor(long monitorId, String username) throws ResourceNotFoundException,
            ForbiddenResourceException {
        BaseMonitor monitor = monitorRepository.findById(monitorId);
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
