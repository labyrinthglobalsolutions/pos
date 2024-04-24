# Use an official Maven image as a base image
FROM maven:3.8.1-openjdk-17 AS builder

# Set the working directory in the container
WORKDIR /app

# Copy the Maven project file
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy the project source code
COPY src ./src

# Compile the application and package it into a JAR file
RUN mvn package -DskipTests

# Use a lighter base image for the final image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the compiled JAR file from the builder stage
COPY --from=builder /app/target/Point_Of_Sale.jar ./Point_Of_Sale.jar

# Expose port 9094
EXPOSE 9094

# Run the JAR file when the container launches
CMD ["java", "-jar", "Point_Of_Sale.jar"]
