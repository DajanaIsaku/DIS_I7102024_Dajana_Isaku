FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /build
COPY . .
RUN mvn -pl discovery-server -am package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /build/discovery-server/target/discovery-server-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
