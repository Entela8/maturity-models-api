FROM openjdk:21-slim

COPY ./target/maturity-models-0.0.1-SNAPSHOT .

EXPOSE 8080

CMD ["sh","-c","java -XX:InitialRAMPercentage=50 -XX:MaxRAMPercentage=70  -XshowSettings $JAVA_OPTS -jar maturity-models-0.0.1-SNAPSHOT"]