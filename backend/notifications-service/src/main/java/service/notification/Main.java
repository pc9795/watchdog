package service.notification;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.notification.actors.ClusterListener;
import service.notification.actors.ClusterNode;
import service.notification.routes.NotificationRoutes;
import service.notification.utils.Constants;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

import static akka.http.javadsl.server.Directives.concat;

/**
 * Purpose: Entry point for the program.
 **/
public class Main {

    private static Logger LOGGER = LoggerFactory.getLogger(Main.class);

    /**
     * Main function
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        try {
            loadConfiguration();

        } catch (IOException e) {
            LOGGER.error("Error in loading configuration!", e);
            System.exit(1);
        }

        startActorSystem();
    }

    /**
     * Start the actor system
     */
    private static void startActorSystem() {
        ActorSystem system = ActorSystem.create("notificationActorSystem");//Create the actor system
        system.actorOf(ClusterListener.props(), Constants.CLUSTER_LISTENER_ACTOR_NAME);//listener actor
        //Create the node actor which represent a single entity in the cluster
        ActorRef node = system.actorOf(ClusterNode.props(Constants.workers), "node");
        //Get management routes from AkkaManagement module.
        Route managementRoutes = AkkaManagement.get(system).getRoutes();

        //Get the routes for notification
        Duration askTimeout = system.settings().config().getDuration("akka.routes.ask-timeout");
        LOGGER.info(String.format("Ask time out retrieved:%s", askTimeout));
        NotificationRoutes notificationRoutes = new NotificationRoutes(node, askTimeout);

        //Setup http server
        Http http = Http.get(system);
        Materializer materializer = Materializer.createMaterializer(system);
        Route allRoutes = concat(managementRoutes, notificationRoutes.routes());
        Flow<HttpRequest, HttpResponse, NotUsed> flow = allRoutes.flow(system, materializer);
        String hostname = system.settings().config().getString("akka.management.http.hostname");
        int port = system.settings().config().getInt("akka.management.http.port");
        http.bindAndHandle(flow, ConnectHttp.toHost(hostname, port), materializer);
        LOGGER.info(String.format("AKKA Http server is running at %s:%s...", hostname, port));
    }

    /**
     * Load the configuration from properties and environment variables
     *
     * @throws IOException if error in loading config from properties file
     */
    private static void loadConfiguration() throws IOException {
        //Loading config from properties file.
        Properties config = new Properties();
        config.load(Main.class.getClassLoader().getResourceAsStream(Constants.configFile));
        LOGGER.info("Properties file is loaded successfully...");
        //Updating config.
        Constants.emailProperties.put("mail.smtp.host", config.getProperty("mail.smtp.host").trim());
        Constants.emailProperties.put("mail.smtp.port", config.getProperty("mail.smtp.port").trim());
        Constants.emailProperties.put("mail.smtp.auth", config.getProperty("mail.smtp.auth").trim());
        Constants.emailProperties.put("mail.smtp.starttls.enable", config.getProperty("mail.smtp.starttls.enable").trim());
        Constants.emailSubject = config.getProperty("subject").trim();
        Constants.emailUsername = config.getProperty("username").trim();
        Constants.emailFromAddr = config.getProperty("from_addr").trim();
        Constants.workers = Integer.parseInt(config.getProperty("workers").trim());
        //Get password from environment variable.
        String password = System.getenv("WATCHDOG_EMAIL_PASSWORD");
        LOGGER.info("Password is retrieved from environment variable...");
        Constants.emailSession = Session.getInstance(Constants.emailProperties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Constants.emailUsername, password);
            }
        });
        LOGGER.info("Session is created to sending email and used by all workers...");
    }
}
