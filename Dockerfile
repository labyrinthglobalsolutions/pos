# Use an official Maven 3.9.6 image with Amazon Corretto 8 as a base image for building
FROM maven:3.9.6-amazoncorretto-8-debian AS builder

# Set the working directory in the container
WORKDIR /app

# Copy the Maven project file
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy the project source code
COPY src ./src

# Clean and package the application
RUN mvn clean package -DskipTests

# Verify the contents of the target directory
RUN ls -la /app/target

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
