#!/usr/bin/env bash
# Locally run the project without docker. This script assumes that all the projects are in the same directory as this
# script is.
# NOTE: THIS SCRIPT ASSUMES THAT COCKROACH-DB AND MONGO-DB ARE RUNNING

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

# Build parent POM; -N means that it will not recurse into child projects
echo ">>>Installing parent project"
mvn -N install

# Installing core in local repo.
echo ">>>Installing core package"
cd core/
mvn clean compile install
check_error "Core project doesn't installed successfully"
cd ..

declare -a dirs=("notifications-service/")
# Build and run NON-SPRING services
for i in "${dirs[@]}"
do
    echo ">>>Building project: $i"
    cd $i
    mvn clean compile
    check_error $i." project is not able to be compiled"
    # Running the service in background
    mvn exec:java &
    check_error $i." service is not able to run"
    # Sleeping for 5 seconds so that out put is not intermingled as much as possible
    sleep 5
    cd ..
done

declare -a dirs=("client-service/" "monitoring-service/")
# Build and run SPRING services.
for i in "${dirs[@]}"
do
    echo ">>>Building project: $i"
    cd $i
    mvn clean compile
    check_error $i." project is not able to be compiled"
    # Running the service in background
    mvn spring-boot:run &
    check_error $i." service is not able to run"
    # Sleeping for 5 seconds so that out put is not intermingled as much as possible
    sleep 5
    cd ..
done

# Run the frontend
cd ../frontend
ng serve &
check_error "Front end is not able to start"