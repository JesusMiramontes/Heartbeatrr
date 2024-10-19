FROM amazoncorretto:21
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

# List of services to be checked for health
ENV HEARTBEATRR_SERVICES_URLS=""