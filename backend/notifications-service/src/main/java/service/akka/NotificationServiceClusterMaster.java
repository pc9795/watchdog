package service.akka;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;

public class NotificationServiceClusterMaster {
    public static void main(String[] args) {
        // note that client is not a compute node, role not defined
        ActorSystem system = ActorSystem.create("ClusterSystem", ConfigFactory.load("notificationService"));
        system.actorOf(Props.create(NotificationService_Cluster_Actor.class, "/user/notificationServiceProxy"), "notificationServiceCluster");
    }
}
