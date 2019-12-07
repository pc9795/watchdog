package service.monitoring.services;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import core.repostiories.cockroachdb.MonitorRepository;
import core.repostiories.mongodb.MonitorLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import service.monitoring.actors.MasterActor;
import service.monitoring.actors.MonitoringProtocol;

import java.util.List;

/**
 * Created By: Prashant Chaubey
 * Created On: 06-12-2019 00:49
 * Purpose: TODO:
 **/
@Component
public class MonitoringInitializerComponent implements CommandLineRunner {
    @Value("${workSize}")
    private int workSize;
    @Value("${master_count}")
    private int masterCount;
    @Value("#{'${masters}'.split(',')}")
    private List<Integer> masterList;
    @Value("${polling_interval}")
    private int pollingInterval;
    private final MonitorRepository monitorRepository;
    private final MonitorLogRepository monitorLogRepository;

    @Autowired
    public MonitoringInitializerComponent(MonitorRepository monitorRepository, MonitorLogRepository monitorLogRepository) {
        this.monitorRepository = monitorRepository;
        this.monitorLogRepository = monitorLogRepository;
    }

    @Override
    public void run(String... args) {
        ActorSystem system = ActorSystem.create("monitoringActorSystem");
        for (Integer parent : masterList) {
            ActorRef master = system.actorOf(Props.create(MasterActor.class, monitorRepository, monitorLogRepository,
                    pollingInterval, masterCount, parent, workSize), String.format("Master_Actor_%s", parent));
            master.tell(new MonitoringProtocol.FindWork(), master);
        }
    }
}
