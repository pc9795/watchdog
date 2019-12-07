package service.monitoring.services;

import core.repostiories.cockroachdb.MonitorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created By: Prashant Chaubey
 * Created On: 06-12-2019 00:49
 * Purpose: TODO:
 **/
@Component
public class MonitoringInitializerComponent implements CommandLineRunner {
    @Value("${master_count}")
    private int parentCount;
    @Value("#{'${masters}'.split(',')}")
    private List<Integer> parent;
    @Value("${polling_interval}")
    private int pollingInterval;
    private final MonitorRepository monitorRepository;

    @Autowired
    public MonitoringInitializerComponent(MonitorRepository monitorRepository) {
        this.monitorRepository = monitorRepository;
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
