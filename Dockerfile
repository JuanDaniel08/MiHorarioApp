# Fase 1: Compilar la aplicación con Maven usando Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
# Compila saltándose los tests para evitar errores de conexión en la nube
RUN mvn clean package -DskipTests

# Fase 2: Ejecutar la aplicación con una imagen ligera de Java 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copia el archivo .jar generado en la fase anterior (se llama demo porque así está en tu pom.xml)
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar app.jar

# Expone el puerto 8081 que configuraste en tu application.properties
EXPOSE 8081

# Comando para arrancar tu servidor Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]