FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY build/libs/git-service-0.0.1-SNAPSHOT.jar /app/git-service.jar
EXPOSE 8080
ENTRYPOINT java -jar /app/git-service.jar --server.port=8080