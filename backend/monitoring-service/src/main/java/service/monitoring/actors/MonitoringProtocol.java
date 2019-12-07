package service.monitoring.actors;

import core.entities.cockroachdb.BaseMonitor;
import core.entities.mongodb.MonitorLog;

import java.util.List;

/**
 * Created By: Prashant Chaubey
 * Created On: 06-12-2019 00:57
 * Purpose: TODO:
 **/
public class MonitoringProtocol {
    public static class FindWork {
    }

    static class AssignWork {
        private List<BaseMonitor> monitors;

        AssignWork(List<BaseMonitor> monitors) {
            this.monitors = monitors;
        }

        List<BaseMonitor> getMonitors() {
            return monitors;
        }
    }

    static class StartWork {

    }

    static class Wait {

    }

    public static class UpdateWork {
        private MonitorLog monitorLog;

        public UpdateWork(MonitorLog monitorLog) {
            this.monitorLog = monitorLog;
        }

        public MonitorLog getMonitorLog() {
            return monitorLog;
        }
    }
}
