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

# Installing core
cd core/
mvn clean compile install
check_error "Core project doesn't installed successfully"
cd ..

declare -a dirs=("auldfellas/" "dodgydrivers/" "girlpower/" "broker/")

# Building and packaging all services into jars.
for i in "${dirs[@]}"
do
    echo ">>>Building project: $i"
    cd $i
    mvn clean compile package
    check_error $i." project not packaged successfully"
    cd ../
done

# Using docker compose to create containers for all service.
docker-compose -p quoco up --build