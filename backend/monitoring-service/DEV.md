**Important URLS**
* http://localhost:8558/cluster/members - health check of the server
* http://localhost:8558/ready
* http://localhost:8558/alive
* akka://monitoringActorSystem@127.0.0.1:25520 - remote address of this actor system. Right now clustering is not enabled
but akka clustering allow us to use akka management and akka remoting. It can be used in future. This url can also be 
found using `selfNode` key in the response returned from the /cluster/members url