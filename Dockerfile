FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

COPY gradle ./gradle
COPY gradlew ./
COPY settings.gradle ./
COPY build.gradle ./
COPY common ./common
COPY controller ./controller
COPY service ./service
COPY domain ./domain
COPY infrastructure ./infrastructure

RUN chmod +x ./gradlew
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

ENV SPRING_CONFIG_NAME=application
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]