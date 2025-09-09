#!/bin/bash

# Create directories for generated files
mkdir -p ../user-service/src/main/java
mkdir -p ../inventory-service/src/main/java
mkdir -p ../event-service/src/main/java
mkdir -p ../gateway-fastapi/empuje

# Generate Java stubs
protoc --java_out=../user-service/src/main/java --grpc-java_out=../user-service/src/main/java -I=. user_service.proto
protoc --java_out=../inventory-service/src/main/java --grpc-java_out=../inventory-service/src/main/java -I=. inventory_service.proto
protoc --java_out=../event-service/src/main/java --grpc-java_out=../event-service/src/main/java -I=. event_service.proto

# Generate Python stubs
python3 -m grpc_tools.protoc -I=. --python_out=../gateway-fastapi/empuje --grpc_python_out=../gateway-fastapi/empuje user_service.proto
python3 -m grpc_tools.protoc -I=. --python_out=../gateway-fastapi/empuje --grpc_python_out=../gateway-fastapi/empuje inventory_service.proto
python3 -m grpc_tools.protoc -I=. --python_out=../gateway-fastapi/empuje --grpc_python_out=../gateway-fastapi/empuje event_service.proto

echo "Generated stubs for Java and Python"
