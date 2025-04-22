FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

COPY gradle ./gradle
COPY gradlew ./
COPY settings.gradle ./
COPY build.gradle ./
COPY apiserver ./apiserver
COPY common ./common  
RUN chmod +x ./gradlew
RUN ./gradlew apiserver:bootJar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/apiserver/build/libs/*.jar app.jar

ENV SPRING_CONFIG_NAME=application-dev
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
