FROM maven:3-openjdk-17-slim as builder
WORKDIR /project
COPY MicroServizi/ReservManager/ .
RUN mvn package

FROM openjdk:17-alpine
WORKDIR /app
COPY --from=builder /project/target/ReservManager-0.0.1-SNAPSHOT.jar ./ReservManager.jar
CMD java -jar ReservManager.jar