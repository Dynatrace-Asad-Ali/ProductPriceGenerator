FROM openjdk:17-jdk-slim-buster
WORKDIR /
COPY LogGenerator.jar .

#COPY app/build/libs/app.jar build/

WORKDIR /
ENTRYPOINT ["java",  "-jar",  "LogGenerator.jar"]