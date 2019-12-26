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
 * Purpose: It will initialize the actor system
 **/
@Component
public class MonitoringInitializerComponent implements CommandLineRunner {
    @Value("${workSize}")
    private int workSize; //The no of monitors to query at at time from database.
    @Value("${master_count}")
    private int masterCount;//Total no of masters
    @Value("#{'${masters}'.split(',')}")
    private List<Integer> masterList;//Masters which will be handled by this service
    @Value("${polling_interval}")
    private int pollingInterval;//Time to wait before next hit to the database for monitors
    private final MonitorRepository monitorRepository; //Access cockroachdb
    private final MonitorLogRepository monitorLogRepository; //Access mongodb

    @Autowired
    public MonitoringInitializerComponent(MonitorRepository monitorRepository, MonitorLogRepository monitorLogRepository) {
        this.monitorRepository = monitorRepository;
        this.monitorLogRepository = monitorLogRepository;
    }

    @Override
    public void run(String... args) {
        ActorSystem system = ActorSystem.create("monitoringActorSystem");
        for (Integer parent : masterList) {
            //Create master actor
            ActorRef master = system.actorOf(Props.create(MasterActor.class, monitorRepository, monitorLogRepository,
                    pollingInterval, masterCount, parent, workSize), String.format("Master_Actor_%s", parent));
            //Seed message for master
            master.tell(new MonitoringProtocol.FindWork(), master);
        }
    }
}
