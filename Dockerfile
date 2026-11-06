# Multi-stage Dockerfile for MONETRA REST API
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy parent POM and module POMs
COPY pom.xml .
COPY monetra-core/pom.xml monetra-core/
COPY monetra-api/pom.xml monetra-api/
COPY monetra-desktop/pom.xml monetra-desktop/

# Copy sources for core and api
COPY monetra-core/src monetra-core/src
COPY monetra-api/src monetra-api/src

# Package monetra-core and monetra-api
RUN mvn clean package -DskipTests -pl monetra-core,monetra-api -am

# Runtime Stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Non-root user for security
RUN groupadd -r monetra && useradd -r -g monetra monetra

COPY --from=builder /app/monetra-api/target/monetra-api-1.0.0-SNAPSHOT.jar app.jar
RUN chown -R monetra:monetra /app

USER monetra

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
