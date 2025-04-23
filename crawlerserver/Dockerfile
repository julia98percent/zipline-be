FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

COPY gradle ./gradle
COPY gradlew ./
COPY settings.gradle ./
COPY build.gradle ./
COPY crawlerserver ./crawlerserver
RUN chmod +x ./gradlew
RUN ./gradlew crawlerserver:bootJar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/crawlerserver/build/libs/*.jar app.jar

ENV SPRING_CONFIG_NAME=application-dev
EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]