# Use an official Maven 3.9.6 image with Amazon Corretto 8 as a base image for building
FROM maven:latest AS builder

# Set the working directory in the container
WORKDIR /app

# Copy only the Maven project file to leverage Docker layer caching
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy the entire project source code
COPY src ./src

# Build the application without debugging output
RUN mvn clean package -DskipTests

# Use a lightweight base image for the final container
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file from the builder stage
COPY --from=builder /app/target/Point_Of_Sale.jar .

# Expose port 9094
EXPOSE 9094

# Command to run the application
CMD ["java", "-jar", "Point_Of_Sale.jar"]
