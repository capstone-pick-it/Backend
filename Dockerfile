FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY settings.gradle .
COPY build.gradle .
COPY src src

RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar -x test

FROM eclipse-temurin:21-jre

WORKDIR /app

RUN groupadd -r appgroup && useradd -r -g appgroup appuser

COPY --from=build /app/build/libs/*.jar app.jar

RUN chown -R appuser:appgroup /app

EXPOSE 8080

USER appuser

ENTRYPOINT ["java", "-jar", "app.jar"]