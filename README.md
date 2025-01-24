# maturity-models-api

When's the first time running the app:
mvn clean install

docker compose up 

To run spring boot:
mvn spring-boot:run

Stop docker:
docker compose down

to manipulate the DB from terminal:
docker exec -it maturity-models-api-database-1 psql -U admin -d maturity_models