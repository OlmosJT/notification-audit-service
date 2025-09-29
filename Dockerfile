# Stage 1: Build the application JAR using a JDK 21 image
FROM eclipse-temurin:21-jdk-jammy as builder
WORKDIR /workspace
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew build -x test --no-daemon


# Stage 2: Create the final, smaller runtime image using a JRE 21 image
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=builder /workspace/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]