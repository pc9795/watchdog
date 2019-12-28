FROM openjdk:11
RUN mkdir -p service/
COPY target/ service/
CMD ["/usr/local/openjdk-11/bin/java", "-jar", "/service/client-service-1.0.jar"]
