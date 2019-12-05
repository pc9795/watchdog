export class Monitor {
  constructor(
    public id: number, public name: string, public ipOrUrlOrHost: string, public monitoringInerval: number, public type: MonitorType,
    public expectedHttpStatusCode?: number, public socketPort?: number
  ) {
  }
}

enum MonitorType {
  PING_MONITOR = 'PingMonitor',
  HTTP_MONITOR = 'HttpMonitor',
  SOCKET_MONITOR = 'SocketMonitor'
}
