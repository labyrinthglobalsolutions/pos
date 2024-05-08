#!/bin/bash

# Find the JAR file in the directory
JAR_FILE=$(ls Point_Of_Sale-*.jar)

# Run the found JAR file
java -jar "$JAR_FILE"
