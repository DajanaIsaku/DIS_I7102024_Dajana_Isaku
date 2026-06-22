FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /build
COPY . .
RUN mvn -pl order-service -am package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /build/order-service/target/order-service-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
