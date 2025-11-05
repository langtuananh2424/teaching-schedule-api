FROM eclipse-temurin:17-jdk-jammy

LABEL maintainer="langtuananh2424@gmail.com"

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
