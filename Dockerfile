# Start with the JDK 22 image
FROM openjdk:22-jdk-slim as build

# Install curl and Maven
RUN apt-get update && \
    apt-get install -y curl && \
    apt-get install -y maven

# Set the working directory in the Docker container
WORKDIR /app

# Copy the local code to the Docker container
COPY . .

# Build the application
RUN mvn clean package -DskipTests

# For the final image, use the same JDK 22 base
FROM openjdk:22-jdk-slim

# Set the working directory in the Docker container
WORKDIR /app

# Copy the JAR file from the build stage to the final stage
COPY --from=build /app/target/*.jar /app/application.jar

# Set the port number the container should expose
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "/app/application.jar"]
