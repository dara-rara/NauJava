FROM openjdk:21-jdk-slim
WORKDIR /app

# Копируем JAR-файл
COPY target/NauJava-0.0.1-SNAPSHOT.jar naujava.jar

# Копируем .env файл
COPY .env .env

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "naujava.jar"]