package service.akka;

import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.cluster.singleton.ClusterSingletonManager;
import akka.cluster.singleton.ClusterSingletonManagerSettings;
import akka.cluster.singleton.ClusterSingletonProxy;
import akka.cluster.singleton.ClusterSingletonProxySettings;
//import akka.management.javadsl.AkkaManagement;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.io.Tcp;

public class MasterMain {

    public static void main(String[] args) {

        startup(new String[] {"2551"});
        NotificationServiceClusterMaster.main(new String[0]);

    }

    public static void startup(String[] ports) {
        for (String port : ports) {
            // Override the configuration of the port
            Config config = ConfigFactory.parseString("akka.remote.classic.netty.tcp.port=" + port)
                            .withFallback(ConfigFactory.parseString("akka.cluster.roles = [compute]"))
                    .withFallback(ConfigFactory.load());

            System.out.println("reached here 0");

            ActorSystem system = ActorSystem.create("ClusterSystem", config);

            System.out.println("reached here 1");

//            AkkaManagement.get(system).start();

            // #create-singleton-manager
            ClusterSingletonManagerSettings settings =
                    ClusterSingletonManagerSettings.create(system).withRole("compute");

            System.out.println("reached here 2");

            system.actorOf(
                    ClusterSingletonManager.props(
//                            Props.create(NotificationService_Supervisor.class),
                            NotificationService_Supervisor.props(1),
                            PoisonPill.getInstance(), settings),
                    "notificationService");
            // #create-singleton-manager
            System.out.println("reached here 3");

            // #singleton-proxy
            ClusterSingletonProxySettings proxySettings =
                    ClusterSingletonProxySettings.create(system).withRole("compute");

            System.out.println("reached here 4");

            system.actorOf(
                    ClusterSingletonProxy.props("/user/notificationService",
                            proxySettings), "notificationServiceProxy");

            System.out.println("reached here 5");
            // #singleton-proxy
        }
    }
}
