FROM openjdk:21-slim

WORKDIR /app
 
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src ./src

RUN chmod +x ./mvnw
RUN ./mvnw dependency:go-offline

EXPOSE 8080

ENTRYPOINT ["./mvnw", "spring-boot:run"]