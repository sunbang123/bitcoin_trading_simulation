FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY build.gradle settings.gradle gradlew /app/
COPY gradle /app/gradle
COPY . /app

RUN chmod +x gradlew
RUN ./gradlew build

CMD ["./gradlew", "bootRun"]
