package service.monitoring.actors;

import core.entities.cockroachdb.BaseMonitor;
import core.entities.mongodb.MonitorLog;

import java.util.List;

/**
 * Created By: Prashant Chaubey
 * Created On: 06-12-2019 00:57
 * Purpose: Messaging protocol between master and actor
 **/
public class MonitoringProtocol {

    /**
     * Message for master to find work
     */
    public static class FindWork {
    }

    /**
     * Message for master to assign monitors to child actors
     */
    static class AssignWork {
        private List<BaseMonitor> monitors;

        AssignWork(List<BaseMonitor> monitors) {
            this.monitors = monitors;
        }

        List<BaseMonitor> getMonitors() {
            return monitors;
        }
    }

    /**
     * Message for child actor to start work
     */
    static class StartWork {

    }

    /**
     * Message for master to wait some time
     */
    static class Wait {

    }

    /**
     * Message for master to update the monitoring log
     */
    static class UpdateWork {
        private MonitorLog monitorLog;

        UpdateWork(MonitorLog monitorLog) {
            this.monitorLog = monitorLog;
        }

        MonitorLog getMonitorLog() {
            return monitorLog;
        }
    }

    /**
     * Message for parent to delete work for deleted monitors.
     */
    static class DeleteWork {

    }

}
