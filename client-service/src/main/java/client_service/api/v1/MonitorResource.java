package client_service.api.v1;

import client_service.entities.BaseMonitor;
import client_service.entities.HttpMonitor;
import client_service.exceptions.ResourceNotExistException;
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
        if(monitorRepository.existsById(baseMonitorId)){
            throw  new ResourceNotExistException("monitor with " + baseMonitorId + " Does not exists");
        }
        return monitorRepository.findById(baseMonitorId);
    }


    @GetMapping
    public List<Monitor> getAllMonitors() {
        //todo implement

        return null;
    }

    @GetMapping("/{user_id}")
    public List<BaseMonitor> getAllMonitorsFromUser(@PathParam("monitor_id") long id){

        return null;
    }

    @GetMapping("/{monitor_id}")
    public Monitor getMonitorById(@PathParam("monitor_id") long id) {
        //todo implement


        return null;
    }

    @GetMapping("/{user_id}/get_monitors/")
    public Monitor getMonitorsByUserId(@PathParam("user_id") long id) {
        //todo implement


        return null;
    }

    //@RequestMapping(method = RequestMethod.POST, params = {})
//    @PostMapping("add_monitor/")
//    public HttpMonitor addMonitor(@PathParam("http_monitor") @Valid HttpMonitor httpMonitor) throws UserDoesntExist {
//        //todo implement
//
//        return monitorRepository.save(httpMonitor);
//    }

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
