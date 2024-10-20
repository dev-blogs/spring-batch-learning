FROM openjdk:11

RUN mkdir -p /usr/app
RUN mkdir -p /usr/app/libs

WORKDIR /usr/app

COPY target/spring-batch-example-0.0.1-SNAPSHOT.jar /usr/app
COPY target/spring-batch-example-0.0.1-SNAPSHOT.lib/* /usr/app/libs

CMD ["java", "-cp", "spring-batch-example-0.0.1-SNAPSHOT.jar:/usr/app/libs/*", "com.example.App"]