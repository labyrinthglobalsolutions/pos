# Use an official Maven image with Amazon Corretto 8 as a base image for building
FROM maven:3.9.6 AS builder

# Set the working directory in the container
WORKDIR /app

# Copy the entire Maven project
COPY . .

# Build the application without debugging output and name the JAR file
RUN mvn clean package -DskipTests

# Use a lightweight base image for the final container
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file(s) from the builder stage
COPY --from=builder /app/target/Point_Of_Sale-*.jar ./

# Expose port 8082
EXPOSE 8082

# Copy the shell script to run the JAR file
COPY start.sh .

# Grant execute permission to the shell script
RUN chmod +x start.sh

# Command to run the application
CMD ["./start.sh"]
