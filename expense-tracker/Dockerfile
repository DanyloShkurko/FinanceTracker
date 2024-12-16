FROM gradle:8.1.1-jdk17 AS build

RUN apt-get update && apt-get install -y openjdk-21-jdk

WORKDIR /app

COPY gradle/wrapper/ gradle/wrapper/
COPY gradlew .
COPY build.gradle.kts settings.gradle.kts ./
COPY src/ src/

RUN ./gradlew build -x test

FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=build /app/build/libs/expense-tracker-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
CMD curl --fail http://localhost:8080/actuator/health || exit 1

CMD ["sh", "-c", "java -Xmx512m -Xms256m -jar app.jar"]