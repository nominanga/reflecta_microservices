FROM gradle:8.12.1 AS build

ARG MODULE
WORKDIR /app

COPY . .

RUN chmod +x gradlew && ./gradlew :${MODULE}:bootJar --no-daemon --parallel --build-cache

FROM amazoncorretto:21

ARG MODULE
WORKDIR /app

COPY --from=build /app/${MODULE}/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]