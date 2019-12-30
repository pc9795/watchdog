NOTE: We are using docker-toolbox so we have to use `192.168.99.100`(docker-machine ip) instead of `localhost`. So if you
are using docker community edition change those things accordingly.  

**Services Table**

Name|Description|port
---|---|---
Mongo db|Mongo db database service|27017
Cockroach db|Cockroach db database service|26257
-|Cockroach db management dashboard|8080
Monitoring service|Akka cluster exposed via Akka HTTP|8558
-|Akka cluster remoting|25520
Notifications service|Akka cluster exposed via Akka HTTP|8559
-|Akka cluster remoting|25521
Client service|Spring rest service with spring security|8081

**Client service**

Swagger documentation is available at `192.168.99.100:8081/swagger-ui.html`.

**Notifications service**

Base url: `192.168.99.100:8559`

Path|HTTP method|Required form fields|Description
---|---|---|---
/cluster/members/|GET|None|	Returns the status of the Cluster in JSON format.
/cluster/members/|POST|address: {address}|Executes join operation in cluster for the provided {address}.
/cluster/members/{address}|	GET|None|Returns the status of {address} in the Cluster in JSON format.
/cluster/members/{address}|	DELETE|None|Executes leave operation in cluster for provided {address}.
/cluster/members/{address}|	PUT|operation: Down	|Executes down operation in cluster for provided {address}.
/cluster/members/{address}|	PUT|operation: Leave|Executes leave operation in cluster for provided {address}.
/cluster/shards/{name}|GET|None	|Returns shard info for the shard region with the provided {name}
/notifications/|POST|`core.beans.EmailMessage` in JSON|Submit an email notification.

**Monitoring service**

Base url: `192.168.99.100:8558`

Path|HTTP method|Required form fields|Description
---|---|---|---
/cluster/members/|GET|None|	Returns the status of the Cluster in JSON format.
/cluster/members/|POST|address: {address}|Executes join operation in cluster for the provided {address}.
/cluster/members/{address}|	GET|None|Returns the status of {address} in the Cluster in JSON format.
/cluster/members/{address}|	DELETE|None|Executes leave operation in cluster for provided {address}.
/cluster/members/{address}|	PUT|operation: Down	|Executes down operation in cluster for provided {address}.
/cluster/members/{address}|	PUT|operation: Leave|Executes leave operation in cluster for provided {address}.
/cluster/shards/{name}|GET|None	|Returns shard info for the shard region with the provided {name}
/monitoring/workers/{monitor_id}|PUT|`core.entities.cocroachdb.BaseMonitor` in JSON|Edit the worker actor with the given monitor
/monitoring/workers/{monitor_id}|DELETE|None|Delete the worker actor with the given monitor
/monitoring/workers/|GET|None|All the workers assigned to particular node; NOTE: This api is only for development purposes to debug as workers can go beyond thousands in real scenario.
/monitoring/workers/{monitor_id}|GET|None|Get the worker assigned to particular monitor

**Actors Fault Tolerance**

Default Supervisor Strategy is used - Escalate is used if the defined strategy doesn’t cover the exception that was thrown.

When the supervisor strategy is not defined for an actor the following exceptions are handled by default:
* `ActorInitializationException` will stop the failing child actor
* `ActorKilledException` will stop the failing child actor
* `DeathPactException` will stop the failing child actor
* `Exception` will restart the failing child actor
* Other types of `Throwable` will be escalated to parent actor

If the exception escalate all the way up to the root guardian it will handle it in the same way as the default strategy defined above.