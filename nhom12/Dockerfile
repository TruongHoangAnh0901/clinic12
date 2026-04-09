# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Build the app, skip tests to make it faster
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# Expose port 8080 (consistent with application.properties)
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx300m", "-Xss512k", "-XX:CICompilerCount=2", "-Dfile.encoding=UTF-8", "-jar", "app.jar"]
