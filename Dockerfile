FROM eclipse-temurin:17
WORKDIR application
ARG JAR=build/libs/*.jar
COPY ${JAR} menu-service.jar
EXPOSE 9091
ENTRYPOINT ["java", "-jar", "menu-service.jar"]