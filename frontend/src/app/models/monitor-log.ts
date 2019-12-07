export class MonitorLog {
  constructor(public id: string, public monitorId: number, public username: string, public status: boolean, public errorMessage: string,
              public creationTime: string) {
  }
}
