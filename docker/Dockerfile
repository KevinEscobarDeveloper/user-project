FROM gradle:8.14-jdk24 AS build

WORKDIR /app

COPY .. .
RUN chmod +x ./gradlew

RUN ./gradlew clean bootJar --no-daemon -x test

FROM eclipse-temurin:24-jre

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8085

CMD ["java", "-jar", "app.jar"]
