package service.notification.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.routing.FromConfig;
import service.notification.protocols.NotificationProtocol;

/**
 * Purpose: Actor which represent a single node in the cluster.
 **/
public class ClusterNode extends AbstractActor {
    private ActorRef router;

    /**
     * Actor configuration object
     *
     * @param workers no of workers for master actor
     * @return configuration object
     */
    public static Props props(int workers) {
        return Props.create(ClusterNode.class, workers);
    }

    public ClusterNode(int workers) {
        //Create the master actor for router to work
        getContext().actorOf(MasterActor.props(workers), "master");
        //Get the router actor from configuration
        //Have to create master router first as it will look for its routees immediately
        this.router = getContext().actorOf(FromConfig.getInstance().props(Props.empty()), "router");
    }

    /**
     * Configure what actions for what messages
     *
     * @return configuration object
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                //Forwarding to router which will select a master worker from any node and forward it to that.
                .match(NotificationProtocol.NotifyEmail.class, obj -> router.forward(obj, getContext()))
                .build();
    }
}
