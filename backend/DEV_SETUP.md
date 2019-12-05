* Run `docker-compose -f docker-compose.dev.yaml up`. It will start the Redis, MongoDB, CockroachDB containers.
* You can check the port details of the services from the docker-compose.yaml
* For Docker toolbox IP address will be 192.168.99.100 and for Docker community edition it remains localhost.
* To change database details use `client-service/src/main/resources/application.properties`