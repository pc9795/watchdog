package client_service.api.v1;

import client_service.repositories.MonitorRepository;
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
//@RestController("/monitors")
public class MonitorResource {

    private final MonitorRepository monitorRepository;

    @Autowired
    public MonitorResource(MonitorRepository monitorRepository) {
        this.monitorRepository = monitorRepository;
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

    @PostMapping
    public Monitor addMonitor(@Valid Monitor monitor) {
        //todo implement
        return null;
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
