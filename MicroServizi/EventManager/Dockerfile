FROM maven:3-openjdk-17-slim as builder
WORKDIR /project
COPY MicroServizi/EventManager/ .
RUN mvn package

FROM openjdk:17-alpine
WORKDIR /app
COPY --from=builder /project/target/EventManager-0.0.1-SNAPSHOT.jar ./EventManager.jar
CMD java -jar EventManager.jar