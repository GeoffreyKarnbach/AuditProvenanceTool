FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY checkstyle.xml .

RUN mvn dependency:go-offline -DskipTests

COPY src ./src
RUN mvn clean package -DskipTests -Dcheckstyle.skip=true

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
