package service.monitoring.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.routing.FromConfig;
import core.repostiories.cockroachdb.MonitorRepository;
import core.repostiories.mongodb.MonitorLogRepository;
import org.aspectj.apache.bcel.classfile.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.monitoring.protocols.MonitoringProtocol;
import service.monitoring.utils.Constants;

/**
 * Purpose: Actor which represent a single node in the cluster.
 **/
public class ClusterNode extends AbstractActor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterNode.class);
    private ActorRef router;
    private ActorRef master;

    /**
     * Actor configuration object
     *
     * @param monitorRepository    repostiory to access monitors
     * @param monitorLogRepository repostiory to access monitor logs
     * @param pollingInterval      interval between db requests
     * @param masterCount          no of master workers
     * @param index                index of this master
     * @param workSize             no of records to pull
     * @return configuration object
     */
    public static Props props(MonitorRepository monitorRepository, MonitorLogRepository monitorLogRepository,
                              int pollingInterval, int masterCount, int index, int workSize, int httpWorkerPool) {
        return Props.create(ClusterNode.class, monitorRepository, monitorLogRepository, pollingInterval, masterCount,
                index, workSize, httpWorkerPool);
    }

    public ClusterNode(MonitorRepository monitorRepository, MonitorLogRepository monitorLogRepository,
                       int pollingInterval, int masterCount, int index, int workSize, int httpWorkerPool) {
        this.master = getContext().actorOf(MasterActor.props(monitorRepository, monitorLogRepository, pollingInterval,
                masterCount, index, workSize, httpWorkerPool), Constants.MASTER_ACTOR_NAME);
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
                //Forwarding to router which will broadcast to all master workers and they will respond to it if they
                //are working on the requested monitor.
                .match(MonitoringProtocol.EditMonitorRequest.class, obj -> router.forward(obj, getContext()))
                .match(MonitoringProtocol.DeleteMonitorRequest.class, obj -> router.forward(obj, getContext()))
                //This will be status of the current node so handled by this node's master.
                .match(MonitoringProtocol.StatusMasterRequest.class, obj -> master.forward(obj, getContext()))
                .match(MonitoringProtocol.StatusWorkerRequest.class, obj -> master.forward(obj, getContext()))
                .build();
    }
}
