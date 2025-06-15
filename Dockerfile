FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# --- Stage 2: Run the application ---
FROM eclipse-temurin:17-jdk-alpine

VOLUME /tmp
WORKDIR /app

# Copy the fat jar from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the port (optional)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
