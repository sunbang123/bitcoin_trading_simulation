FROM gradle:8.5-jdk21 AS build

WORKDIR /workspace
COPY build.gradle settings.gradle ./
COPY src ./src
RUN gradle bootJar --no-daemon

FROM eclipse-temurin:21-jre

WORKDIR /app
RUN useradd --system --create-home spring
COPY --from=build /workspace/build/libs/*.jar app.jar
RUN mkdir -p /app/data && chown -R spring:spring /app

USER spring
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
