package service.notification.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.routing.FromConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.notification.protocols.NotificationProtocol;
import service.notification.utils.Constants;

/**
 * Purpose: Actor which represent a single node in the cluster.
 **/
public class ClusterNode extends AbstractActor {
    private static Logger LOGGER = LoggerFactory.getLogger(ClusterNode.class);
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
        getContext().actorOf(MasterActor.props(workers), Constants.MASTER_ACTOR_NAME);
        //Get the router actor from configuration
        //Have to create master router first as it will look for its routees immediately
        this.router = getContext().actorOf(FromConfig.getInstance().props(Props.empty()), Constants.ROUTER_ACTOR_NAME);
        LOGGER.warn("Router actor created...");
        LOGGER.warn(String.format("Actor created:%s", getSelf().toString()));
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
