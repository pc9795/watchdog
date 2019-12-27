package service.monitoring.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.routing.FromConfig;
import core.repostiories.cockroachdb.MonitorRepository;
import core.repostiories.mongodb.MonitorLogRepository;
import service.monitoring.protocols.MonitoringProtocol;

/**
 * Purpose: Actor which represent a single node in the cluster.
 **/
public class ClusterNode extends AbstractActor {
    private ActorRef router;
    private ActorRef master;
    private MonitorRepository monitorRepository;
    private MonitorLogRepository monitorLogRepository;
    private int pollingInterval;
    private int masterCount;
    private int index;
    private int workSize;


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
                              int pollingInterval, int masterCount, int index, int workSize) {
        return Props.create(ClusterNode.class, monitorRepository, monitorLogRepository, pollingInterval, masterCount,
                index, workSize);
    }

    public ClusterNode(MonitorRepository monitorRepository, MonitorLogRepository monitorLogRepository,
                       int pollingInterval, int masterCount, int index, int workSize) {
        this.master = getContext().actorOf(MasterActor.props(monitorRepository, monitorLogRepository, pollingInterval, masterCount,
                index, workSize), "master");
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
                .match(MonitoringProtocol.EditMonitorRequest.class, obj -> router.forward(obj, getContext()))
                .match(MonitoringProtocol.DeleteMonitorRequest.class, obj -> router.forward(obj, getContext()))
                .match(MonitoringProtocol.StatusMasterRequest.class, obj -> master.forward(obj, getContext()))
                .match(MonitoringProtocol.StatusWorkerRequest.class, obj -> master.forward(obj, getContext()))
                .build();
    }
}
