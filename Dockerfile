FROM openjdk:11

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

COPY target/spring-batch-example-0.0.1-SNAPSHOT.jar /usr/src/app

CMD ["java", "-jar", "spring-batch-example-0.0.1-SNAPSHOT.jar"]