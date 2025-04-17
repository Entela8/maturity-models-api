# maturity-models-api

When's the first time running the app:
```sh
mvn clean install
```

Run docker:
```sh
docker compose up
```

Run spring boot:
```sh
mvn spring-boot:run
```

Stop docker:
```sh
docker compose down
```

To manipulate the DB from terminal (you don't probably have to do this):
```sh
docker exec -it maturity-models-api-database-1 psql -U admin -d maturity_models
```



<configuration>

    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/maturity-models.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>logs/maturity-models-api.%d{yyyy-MM-dd}.%i.log.gz</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d %-5level [%thread] %logger{0}: %msg%n</pattern>
            <outputPatternAsHeader>true</outputPatternAsHeader>
            <charset>utf8</charset>
        </encoder>
    </appender>

    <!-- Debugging to console -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <layout class="ch.qos.logback.classic.PatternLayout">
            <Pattern>%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n</Pattern>
        </layout>
    </appender>

    <root level="debug">
        <appender-ref ref="FILE"/>
        <appender-ref ref="CONSOLE"/>
    </root>

</configuration>