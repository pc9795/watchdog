package service.notification.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Purpose: Listener to log cluster events.
 **/
public class ClusterListener extends AbstractActor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterListener.class);
    private Cluster cluster = Cluster.get(getContext().getSystem());

    /**
     * Actor configuration object
     *
     * @return configuration object
     */
    public static Props props() {
        return Props.create(ClusterListener.class);
    }

    ClusterListener() {
        LOGGER.warn(String.format("Actor created:%s", getSelf().toString()));
    }

    /**
     * Before start hook
     *
     * @throws Exception if error
     */
    @Override
    public void preStart() throws Exception {
        // Subscribe to cluster events
        cluster.subscribe(
                getSelf(), ClusterEvent.initialStateAsEvents(), ClusterEvent.MemberEvent.class,
                ClusterEvent.UnreachableMember.class
        );
    }

    /**
     * After stop hook
     */
    @Override
    public void aroundPostStop() {
        //Unsubscribe to events
        cluster.unsubscribe(getSelf());
    }

    /**
     * Configure what actions to what messages
     *
     * @return configuration object.
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ClusterEvent.MemberUp.class, obj -> LOGGER.info(String.format("Member is up:%s", obj.member())))
                .match(ClusterEvent.UnreachableMember.class,
                        obj -> LOGGER.error(String.format("Member is unreachable:%s", obj.member())))
                .match(ClusterEvent.MemberRemoved.class,
                        obj -> LOGGER.error(String.format("Member is removed:%s", obj.member())))
                .build();
    }
}
