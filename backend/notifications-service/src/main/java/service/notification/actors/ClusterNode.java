package service.notification.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.routing.FromConfig;
import service.notification.protocols.NotificationProtocol;

/**
 * Created By: Prashant Chaubey
 * Created On: 27-12-2019 03:43
 * Purpose: TODO:
 **/
public class ClusterNode extends AbstractActor {
    private ActorRef masterActor;
    private ActorRef router;

    public static Props props(int workers) {
        return Props.create(ClusterNode.class, workers);
    }

    public ClusterNode(int workers) {
        //Create the master actor
        this.masterActor = getContext().actorOf(MasterActor.props(workers), "master");
        //Get the router actor from configuration
        //Have to create master router first as it will look for its routees immediately
        this.router = getContext().actorOf(FromConfig.getInstance().props(Props.empty()), "router");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(NotificationProtocol.NotifyEmail.class, obj -> router.forward(obj, getContext()))
                .build();
    }
}
