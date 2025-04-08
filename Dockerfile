FROM openjdk:21-slim

COPY ./target/*.jar app.jar

EXPOSE 8080

CMD ["sh", "-c", "java -XX:InitialRAMPercentage=50 -XX:MaxRAMPercentage=70 -XshowSettings:vm $JAVA_OPTS -jar app.jar"]