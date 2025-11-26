
# Build stage
FROM eclipse-temurin:21-jdk AS build

# Set working directory inside the container
WORKDIR /app

# Copy the compiled java application JAR file inot the container
COPY ./target/roomify-0.0.1-SNAPSHOT.jar /app

# Expose port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "roomify-0.0.1-SNAPSHOT.jar"]