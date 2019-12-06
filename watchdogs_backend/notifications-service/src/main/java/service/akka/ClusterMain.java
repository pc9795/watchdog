package service.akka;

import java.util.concurrent.TimeUnit;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.Cluster;

public class ClusterMain {

    public static void main(String[] args) {

        final Config config =
                ConfigFactory.parseString("akka.cluster.roles = [notificationService]")
                        .withFallback(ConfigFactory.load("factorial"));

        final ActorSystem system = ActorSystem.create("ClusterSystem", config);



    }

}
