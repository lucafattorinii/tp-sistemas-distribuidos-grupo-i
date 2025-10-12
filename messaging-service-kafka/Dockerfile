FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copiar archivos de construcción
COPY . .

# Construir la aplicación
RUN ./mvnw -DskipTests clean package

# Exponer puerto
EXPOSE 8084

# Comando para ejecutar
CMD ["java", "-jar", "target/messaging-service-kafka-1.0.0.jar"]
