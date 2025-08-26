# Build stage
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install

# Package stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8090

# Instala netcat para hacer check de puerto
RUN apt-get update && apt-get install -y netcat

COPY wait-for-port.sh /wait-for-port.sh
RUN chmod +x /wait-for-port.sh

ENTRYPOINT ["/wait-for-port.sh", "8090", "java", "-jar", "app.jar"]
