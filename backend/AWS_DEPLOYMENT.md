* Create an AWS EC2 instance with Ubuntu 64 bit AMI and t2.small instance with 2 GB ram.
* Update the url of the instance in `frontedn/src/environments/environment.prod.ts`
```
export const environment = {
  production: true,
  server: 'http://<ip of instance>:<port of client service>/'
};
```
* Run `build.sh` and compress **backend** folder using 7z in your local machine.
* Move the artifact **backend.7z** to instance using ssh.
* `sudo apt update -y` - Update `apt` repositories
* `sudo apt install docker.io -y` - Install docker
* `sudo apt install docker-compose -y` - Install docker-compose
*  `sudo apt-get install p7zip-full` - Install 7zip 
* `7z x backend.7z`  - extract project
* `sudo docker-compose up -d` - Run all the services 
