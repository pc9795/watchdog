package service.client.api.v1;

import service.client.entities.BaseMonitor;
import service.client.exceptions.ResourceNotExistException;
import service.client.repositories.MonitorRepository;
import service.client.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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


}
