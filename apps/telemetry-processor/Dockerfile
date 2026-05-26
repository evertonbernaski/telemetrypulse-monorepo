FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace

COPY apps/telemetry-processor/.mvn .mvn
COPY apps/telemetry-processor/mvnw apps/telemetry-processor/mvnw.cmd apps/telemetry-processor/pom.xml ./
RUN chmod +x ./mvnw && ./mvnw -q -DskipTests dependency:go-offline

COPY apps/telemetry-processor/src src
RUN ./mvnw -q package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /workspace/target/telemetry-processor-0.1.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
