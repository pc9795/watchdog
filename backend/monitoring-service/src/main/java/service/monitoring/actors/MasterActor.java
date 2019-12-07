package service.monitoring.actors;

import akka.actor.AbstractActor;
import core.repostiories.cockroachdb.MonitorRepository;

/**
 * Created By: Prashant Chaubey
 * Created On: 06-12-2019 00:56
 * Purpose: TODO:
 **/
public class MasterActor extends AbstractActor {
    private MonitorRepository repository;
    private int pollingInterval;

    public MasterActor(MonitorRepository repository, int pollingInterval) {
        this.repository = repository;
        this.pollingInterval = pollingInterval;
    }

    @Override
    public Receive createReceive() {
        return null;
    }
}
