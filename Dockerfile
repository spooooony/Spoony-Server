FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY build/libs/*.jar spoony-dev.jar
EXPOSE 8080
CMD ["java", "-jar", "spoony-dev.jar"]