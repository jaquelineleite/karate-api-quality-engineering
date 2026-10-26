FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/pix-service-*.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
