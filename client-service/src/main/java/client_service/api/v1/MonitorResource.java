package client_service.api.v1;

import client_service.entities.HttpMonitor;
import client_service.exceptions.UserDoesntExist;
import client_service.repositories.MonitorRepository;
import client_service.repositories.UserRepository;
import client_service.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.management.monitor.Monitor;
import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.util.List;

/**
 * Created By: Prashant Chaubey
 * Created On: 22-11-2019 00:33
 * Purpose: TODO:
 **/
@RestController
@RequestMapping(Constants.ApiV1Resource.MONITORS)
public class MonitorResource {

    private final MonitorRepository monitorRepository;
    private final UserRepository userRepository;

    @Autowired
    public MonitorResource(MonitorRepository monitorRepository, UserRepository userRepository) {
        this.monitorRepository = monitorRepository;
        this.userRepository = userRepository;
    }


    @GetMapping
    public List<Monitor> getAllMonitors() {
        //todo implement
        return null;
    }

    @GetMapping("/{monitor_id}")
    public Monitor getMonitor(@PathParam("monitor_id") long id) {
        //todo implement
        return null;
    }

    //@RequestMapping(method = RequestMethod.POST, params = {})
    @PostMapping
    public HttpMonitor addMonitor(@PathParam("http_monitor") @Valid HttpMonitor httpMonitor) throws UserDoesntExist {
        //todo implement
        long monitors_UserId = httpMonitor.getUser().getId();
        if(!userRepository.existsById(monitors_UserId)){
            // Then user doesnt exist
            throw new UserDoesntExist(monitors_UserId);
        }
        // Else user does exist, so add monitor
        return monitorRepository.save(httpMonitor);
    }

    @PutMapping("/{monitor_id}")
    public void updateMonitor(@PathParam("monitor_id") long id) {
        //todo implement
    }

    @DeleteMapping
    public void deleteMonitor(@PathParam("monitor_id") long id) {
        //todo implement
    }

    @GetMapping("/{status}")
    public void getMonitorStatus(@PathParam("monitor_id") long id) {
        //todo implement
    }
}
