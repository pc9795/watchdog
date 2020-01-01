#Steps to run the project
## Prerequisites
### 1. Install Maven
Check installation is working by `maven --version`. Our version is 3.5.2.
### 2. Install JDK 11
Some Akka modules we used are not backward compatible with JDK 8. Make sure to point your `JAVA_HOME` environment variable 
to the JDK 11 installation. Check the version using `java --version`
### 3. Install Angular CLI
To install Angular CLI we need `npm`. 
* Install node for your operating system it will install `npm` along with it. Check the installation using 
`node --version` and `npm --version`. Our node version is v10.15.3 and npm version is 6.4.1.
* Run `npm install -g @angular/cli`. Check version using `ng --version`. Our Angular CLI version is 7.3.9.
### 4. Install Cockroach DB and Mongo DB
We used docker containers instead of original installed application. 

Docker-compose configuration for starting a single node cluster without authentication.
```
cockroachdb:
    image: cockroachdb/cockroach
    command: start-single-node --insecure
    ports:
      - "26257:26257"
      - "8080:8080"
```
Docker-compose configuration for starting a single node mongodb cluster without authentication
```
 mongo:
    image: mongo
    ports:
      - "27017:27017"
```


## Run system locally
We have created a script `local_run.sh` to automate this process. But as this script will run 4 processes in backend:
client-service, monitoring-service, notifications-service and frontend so the logs could be intermingled. In case of 
any failures it will be hard too debug so following are detail steps used in script. Anyways you can use that script
for reference. That script is supposed to be run from `backend` folder so all paths are relative to that.
### 1. Installing parent project
Go to the `backend` directory and run `mvn -N install`. It will install `watchdog` parent project to local maven 
repository.
### 2. Building core module
Go to the `backend/core` directory and run `mvn clean compile install`. It will install `core` module to local maven 
repository.
### 3. Running notifications-service
Go to the `backend/notifications-service` and run `mvn clean compile`. After it run `mvn exec:java` it will start the
micro service. You can check it is working or not by `http://localhost:8559/alive` it will show OK.
NOTE: Currently the main line which sends the email is commented out in 
`backend\notifications-service\src\main\java\service\notification\utils\Utils.java` line no 36.
```
LOGGER.info(String.format("Going to send message:%s", message));
//todo uncomment
//Transport.send(messageObj);
LOGGER.info("Message sent successfully...");
```
If you wish to send real emails then we have created a temporary email id which is configured in properties file. 
You have to first uncommment this line and create an environment variable by the name: **WATCHDOG_EMAIL_PASSWORD**.
The password/value of this variable is in Appendix section of the project report. Please restart your IDE after creating
the environment variable as some tend to load them on start.
### 4. Running monitoring-service
If you are using Docker tool box and  created containers for MongoDB and CockroachDB then everything is fine. 
The values in `application.properties` will work. In any other case or database connection things. Go to
`backend\monitoring-service\src\main\resources\application.properties` and adjust following values accordingly.
```
spring.jpa.generate-ddl=true
spring.datasource.url=jdbc:postgresql://192.168.99.100:26257/defaultdb
spring.datasource.username=root
spring.data.mongodb.uri=mongodb://192.168.99.100:27017/local
```
For the first run we need `spring.jpa.generate-ddl` to be `true` as it will automatically create the schema in 
Cockroach DB. For further runs we can turn-off this. We are using the default databases for both Cockroach DB(`defaultDB`)
and Mongo DB(`local`). We are not using any authentication so if you are then adjust the Spring properties accordingly.

Go to the `backend/monitoring-service` and run `mvn clean compile`. After it run `mvn spring-boot:run` it will start the
micro service. You can check it is working or not by `http://localhost:8558/alive` it will show OK.
### 5. Running client-service
For configuring databases in `application.properties` follow instructions given in **Running monitoring-service**. Go to
the `backend/client-service` and run `mvn clean compile`. After it run `mvn spring-boot:run` it will start the micro 
service. You can check it is working or not by `http://localhost:8081/swagger-ui.html` it will show OK.

NOTE: To test out this service using Postman is a good idea. As we integrated Swagger but it doesn't support polymorphic
REST resources. So the instructions written on it will not work. Sample for all requests are given in 
`Watchdog.postman_collection.json` in the `backend` director. Install Postman and import this collection to test out
various REST APIs.
### 6. Running front-end code
Go to the `fronend` directory and run `ng serve`. It will take a while as there is problem in `ng` commands: 
`https://github.com/angular/angular-cli/issues/3484`. You can see a message that a server is running at `localhost:4200`.
You can create an user from the front-end and then use our application.

Access the application at `localhost:4200`

## Run using docker
### 1. Setting up the front-end configuration
Update the url of the exposed client-service in `frontend/src/environments/environment.prod.ts`.
```
export const environment = {
  production: true,
  server: 'http://192.168.99.100:8081/'
};
```
We are using docker tool box so the client service is accessed by `http://192.168.99.100:8081/`. The host is the hostname
of the docker-machine. So adjust it accordingly.

### 2. Build the project.
Use the `build.sh`. That's it. We tried to carefully create this build script. It is assumed to be run at the 
`backend/` directory. If you want to debug a particular part you can follow through the script it is well documented 
or you can reach out to us.

Access the application at `192.168.99.100:8081`. NOTE: We are using docker toolbox so adjust you URL accordingly.

## Deploy on AWS instance
### 1. Create an AWS instance
Create an AWS EC2 instance with Ubuntu 64 bit AMI and t2.small instance with 2 GB ram.
### 2. Update AWS url in front-end
Update the url of the instance in `frontend/src/environments/environment.prod.ts`
```
export const environment = {
  production: true,
  server: 'http://<ip of instance>:<port of client service>/'
};
```
### 3. Packaging up the Project
Comment the last line of `build.sh` which triggers a docker build.
```
# Using docker compose to create containers for all service.
docker-compose -p watchdog up --build
```
Run `build.sh` and then compress **backend** folder using 7z in your local machine. NOTE: Build script will package 
front-end code inside client-service.
### 4. Setting up the instance
* Move the artifact **backend.7z** to instance using ssh.
* `sudo apt update -y` to Update `apt` repositories
* `sudo apt install docker.io -y` to Install docker
* `sudo apt install docker-compose -y` to Install docker-compose
*  `sudo apt-get install p7zip-full` to Install 7zip 
* `7z x backend.7z`  to extract project
### 5. Running the project
* Go to the root of extracted project(`backend/`) and run `sudo docker-compose -p watchdog up --build`.
