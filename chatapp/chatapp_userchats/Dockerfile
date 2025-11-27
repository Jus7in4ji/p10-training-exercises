FROM eclipse-temurin:25-jdk
WORKDIR /app
COPY target/chatapp_userchats-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
