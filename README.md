# maturity-models-API
Maturity Models API

## Projet

**Maturity Models** est une application d’évaluation des équipes selon des **modèles de maturité** (Scrum, cybersécurité, qualité, agile, etc.).  
Chaque modèle est composé d’un ensemble de questions avec des niveaux de maturité allant de 1 à 5.

---

## Participants

- **Entela MEMA** - [`Entela8`](https://github.com/Entela8)  
- **Corentin MAURY** - [`BattlaGame`](https://github.com/BattlaGame)  

---

## Fonctionnalités principales

- Lors de l'inscription, je dois choisir un **profil** :
  - **Owner**
  - **Team Leader**
  - **Team Member**

- En tant qu’**owner** :
  - Je peux créer un **modèle de maturité**
    - Il contient un **titre** et une **liste de questions**
    - Chaque question propose **5 réponses** (de 1 à 5) représentant le niveau de maturité

- En tant que **Team Leader** :
  - Je peux **inviter des Team Members** (par email) dans mon équipe

- En tant que **Team Member** :
  - Je réponds aux **questions de l’évaluation**
  - Je peux consulter les résultats sur un **diagramme radar**
    - Chaque axe correspond à une question
    - Les réponses de chaque participant y figurent
    - La **moyenne** est également représentée

---

## Lancer le projet

### Première installation

```sh
mvn clean install
```

### Lancer Docker

```sh
docker compose up
```

### Lancer Spring Boot

```sh
mvn spring-boot:run
```

### Arrêter Docker

```sh
docker compose down
```

### Accéder à la base de données (optionnel)

```sh
docker exec -it maturity-models-api-database-1 psql -U admin -d maturity_models
```