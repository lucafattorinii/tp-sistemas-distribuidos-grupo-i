FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY . /app
RUN ./mvnw -DskipTests clean package

FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080 50052
ENTRYPOINT ["java","-jar","/app/app.jar"]
