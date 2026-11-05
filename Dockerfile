# ---- Build stage: compile the app with the Maven wrapper ----
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Download dependencies first so they are cached until pom.xml changes
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw -q dependency:go-offline

COPY src src
RUN ./mvnw -q clean package -DskipTests

# ---- Runtime stage: only the JRE and the jar ----
FROM eclipse-temurin:25-jre
WORKDIR /app

# Run as an unprivileged user
RUN useradd --system --no-create-home geo
USER geo

COPY --from=build /app/target/geo-agent-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

# GOOGLE_API_KEY must be supplied at runtime (docker run -e GOOGLE_API_KEY=...)
ENTRYPOINT ["java", "-jar", "app.jar"]
