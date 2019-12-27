package service.monitoring.protocols;

import core.entities.cockroachdb.BaseMonitor;
import core.entities.mongodb.MonitorLog;

import java.util.List;

/**
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
    public static class AssignWork {
        private List<BaseMonitor> monitors;

        public AssignWork(List<BaseMonitor> monitors) {
            this.monitors = monitors;
        }

        public List<BaseMonitor> getMonitors() {
            return monitors;
        }
    }

    /**
     * Message for child actor to start work
     */
    public static class StartWork {

    }

    /**
     * Message for master to wait some time
     */
    public static class Wait {

    }

    /**
     * Message for master to update the monitoring log
     */
    public static class UpdateWork {
        private MonitorLog monitorLog;

        public UpdateWork(MonitorLog monitorLog) {
            this.monitorLog = monitorLog;
        }

        public MonitorLog getMonitorLog() {
            return monitorLog;
        }
    }

    /**
     * Message for parent to delete work for deleted monitors.
     */
    public static class DeleteWork {

    }

}
