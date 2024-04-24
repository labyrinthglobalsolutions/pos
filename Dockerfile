# Use an official Maven image as a base image for building
FROM maven:3.8.1-openjdk-17 AS builder

# Set the working directory in the container
WORKDIR /app

# Copy the Maven project file
COPY pom.xml .

# Download dependencies and package the application
COPY src ./src
RUN mvn package -DskipTests

# Use a lightweight base image for the final container
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/target/Point_Of_Sale.jar ./Point_Of_Sale.jar

# Expose port 9094
EXPOSE 9094

# Command to run the application
CMD ["java", "-jar", "Point_Of_Sale.jar"]
