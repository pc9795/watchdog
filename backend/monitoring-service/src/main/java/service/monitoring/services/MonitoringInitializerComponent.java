package service.monitoring.services;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.management.javadsl.AkkaManagement;
import core.repostiories.cockroachdb.MonitorRepository;
import core.repostiories.mongodb.MonitorLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import service.monitoring.actors.ClusterListener;
import service.monitoring.actors.MasterActor;
import service.monitoring.protocols.MonitoringProtocol;

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

    /**
     * Entry point for the command line runner component
     *
     * @param args passed arguments
     */
    @Override
    public void run(String... args) {
        ActorSystem system = ActorSystem.create("monitoringActorSystem");
        //Create the cluster listener actor
        system.actorOf(ClusterListener.props(), "clusterListener");
        AkkaManagement.get(system).start();

        for (Integer parent : masterList) {
            //Create master actor
            ActorRef master = system.actorOf(MasterActor.props(monitorRepository, monitorLogRepository,
                    pollingInterval, masterCount, parent, workSize), String.format("Master_Actor_%s", parent));
            //Seed message for master
            master.tell(new MonitoringProtocol.FindWork(), master);
        }
    }
}
