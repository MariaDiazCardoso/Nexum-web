# ── Etapa 1: compilar el backend con Maven ──
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ── Etapa 2: imagen ligera solo con el JAR ──
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
# El puerto lo toma server.port=${PORT:8081}; las plataformas inyectan PORT.
ENTRYPOINT ["java", "-jar", "app.jar"]
