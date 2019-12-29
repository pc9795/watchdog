#!/usr/bin/env bash
# Build and deploy the application. This script assumes that all the projects are in the same directory as this script
# is and docker is up and running.
# Most of the commands run in parallel(whenever possible) so output could be messy.

# Method to check a command worked or not.
check_error(){
    if [[ $? != '0' ]]; then
        echo "=========================================="
        echo "<<Previous command not ran successfully>>"
        echo "<<ERROR>>:".$1
        echo "EXITING..."
        echo "=========================================="
        exit
    fi
}

# Building front-end code
echo ">>>Building front-end code"
cd ../frontend/
ng build --prod
check_error "Not able to build front end code"
if [[ -d "../backend/client-service/src/main/resources/static" ]]
then
    echo ">>>Static directory exists; Deleting..."
    rm -r ../backend/client-service/src/main/resources/static
    check_error "Not able to delete already existing static directory"
fi
mkdir -p ../backend/client-service/src/main/resources/static
check_error "Not able to create a static directory in client-service"
# It is assumed that name of the angular project is not changed from front end.
mv -v ./dist/frontend/* ../backend/client-service/src/main/resources/static/
check_error "Not able to move built artifacts to client-service"
cd ../backend/

# Build parent POM; -N means that it will not recurse into child projects
echo ">>>Installing parent project"
mvn -N install

# Installing core in local repo.
echo ">>>Installing core package"
cd core/
mvn clean compile install
check_error "Core project doesn't installed successfully"
cd ..

declare -a dirs=("notifications-service/" "monitoring-service/" "client-service/")
# Building and packaging all services into jars.
for i in "${dirs[@]}"
do
    echo ">>>Building project: $i"
    cd $i
    mvn -P prod clean compile package -DskipTests
    check_error $i." project not packaged successfully"
    cd ../
done

# Using docker compose to create containers for all service.
docker-compose -p watchdog up --build