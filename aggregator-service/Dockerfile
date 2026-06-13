FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY common-events/pom.xml common-events/pom.xml
COPY common-kafka/pom.xml common-kafka/pom.xml
COPY bank-service/pom.xml bank-service/pom.xml
COPY credit-card-service/pom.xml credit-card-service/pom.xml
COPY investment-service/pom.xml investment-service/pom.xml
COPY aggregator-service/pom.xml aggregator-service/pom.xml
RUN mvn -pl aggregator-service -am dependency:go-offline -B
COPY common-events/src common-events/src
COPY common-kafka/src common-kafka/src
COPY aggregator-service/src aggregator-service/src
RUN mvn -pl aggregator-service -am package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --from=build /app/aggregator-service/target/*.jar app.jar
USER app
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health | grep -q '"status":"UP"' || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
