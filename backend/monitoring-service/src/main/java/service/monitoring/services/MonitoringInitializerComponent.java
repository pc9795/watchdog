package service.monitoring.services;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.Route;
import akka.management.javadsl.AkkaManagement;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import core.repostiories.cockroachdb.MonitorRepository;
import core.repostiories.mongodb.MonitorLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import service.monitoring.actors.ClusterListener;
import service.monitoring.actors.ClusterNode;
import service.monitoring.routes.MonitoringRoutes;
import service.monitoring.utils.Constants;

import java.time.Duration;

import static akka.http.javadsl.server.Directives.concat;

/**
 * Purpose: It will initialize the actor system
 **/
@Component
public class MonitoringInitializerComponent implements CommandLineRunner {
    private static Logger LOGGER = LoggerFactory.getLogger(MonitoringInitializerComponent.class);
    @Value("${workSize}")
    private int workSize; //The no of monitors to query at at time from database.
    @Value("${master_count}")
    private int masterCount;//Total no of masters
    @Value("${index}")
    private int index;// Index of the master handled by this node
    @Value("${polling_interval}")
    private int pollingInterval;//Time to wait before next hit to the database for monitors
    @Value("${http_worker_pool}")
    private int httpWorkerPool;//No of http workers maintained by master actor
    private final MonitorRepository monitorRepository; //Access monitors
    private final MonitorLogRepository monitorLogRepository; //Access monitor logs
    @Value("${notify_message_url}")
    private String notifyMessageURL;

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
        //Updating some run-time constants
        Constants.notifyMessageURL = notifyMessageURL;

        ActorSystem system = ActorSystem.create(Constants.ACTOR_SYSTEM_NAME);
        system.actorOf(ClusterListener.props(), Constants.CLUSTER_LISTENER_ACTOR_NAME);//Create the cluster listener actor
        //Create the node actor which represent a single entity in the cluster
        ActorRef node = system.actorOf(ClusterNode.props(monitorRepository, monitorLogRepository, pollingInterval,
                masterCount, index, workSize, httpWorkerPool), "node");
        //Get management routes from AkkaManagement module.
        Route managementRoutes = AkkaManagement.get(system).getRoutes();

        //Get the routes for notification
        Duration askTimeout = system.settings().config().getDuration("akka.routes.ask-timeout");
        LOGGER.info(String.format("Ask time out retrieved:%s", askTimeout));
        MonitoringRoutes monitoringRoutes = new MonitoringRoutes(node, askTimeout);

        //Setup http server
        Http http = Http.get(system);
        Materializer materializer = Materializer.createMaterializer(system);
        Route allRoutes = concat(managementRoutes, monitoringRoutes.routes());
        Flow<HttpRequest, HttpResponse, NotUsed> flow = allRoutes.flow(system, materializer);
        String hostname = system.settings().config().getString("akka.management.http.hostname");
        int port = system.settings().config().getInt("akka.management.http.port");
        http.bindAndHandle(flow, ConnectHttp.toHost(hostname, port), materializer);
        LOGGER.info(String.format("AKKA Http server is running at %s:%s...", hostname, port));
    }
}
