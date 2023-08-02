FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /app
COPY build.gradle settings.gradle gradlew ./
COPY gradle/ ./gradle/
RUN ./gradlew --version
COPY src/ ./src/
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/build/libs/git-service*.jar /app/git-service.jar
EXPOSE 80
ENTRYPOINT java -jar /app/git-service.jar --server.port=80