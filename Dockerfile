# Use an official OpenJDK 17 image as a base image
FROM openjdk:17-jdk-slim AS builder

# Set the working directory in the container
WORKDIR /app

# Copy the Maven project file
COPY pom.xml .

# Download dependencies
RUN apt-get update && apt-get install -y maven
RUN mvn dependency:go-offline -B

# Copy the project source code
COPY src ./src

# Compile the application
RUN mvn package -DskipTests

# Use a lighter base image for the final image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the compiled JAR file from the builder stage
COPY --from=builder /app/target/your-application.jar ./your-application.jar

# Expose port 9094
EXPOSE 9094

# Run the JAR file when the container launches
CMD ["java", "-jar", "your-application.jar"]
