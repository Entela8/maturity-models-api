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