FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY build.gradle settings.gradle gradlew /app/
COPY gradle /app/gradle
COPY . /app

RUN chmod +x gradlew

# 캐시 문제 방지를 위해 clean build + 데몬 비활성화
RUN ./gradlew clean build --no-daemon

CMD ["./gradlew", "bootRun", "--no-daemon"]
