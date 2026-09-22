# User Stories og status

## User Story 1 – Hent danske movies fra TMDb

**User Story:**  
Som klient vil jeg kunne hente danske movies fra TMDb, så de kan gemmes og bruges i backend-systemet.

**Acceptance Criteria:**
- Der hentes danske movies fra de sidste 5 år.
- Data hentes fra TMDb REST API.
- JSON-data konverteres til DTO'er med Jackson.
- Movie details og credits hentes fra TMDb.
- Movies, actors, directors og genres gemmes i PostgreSQL.

**Tasks:**
- Opret ApiReader med HttpClient.
- Opret forbindelse til TMDb API.
- Opret DTO'er til TMDb-data.
- Parse JSON med Jackson ObjectMapper.
- Hent movie details og credits.
- Konverter DTO'er til entities.
- Gem data via Service Layer og DAO Layer.

**Status:** Færdig


## User Story 2 – Gem movies og relationer i databasen

**User Story:**  
Som klient vil jeg kunne gemme movies med deres actors, director og genres, så alle relevante oplysninger findes i databasen.

**Acceptance Criteria:**
- Movies gemmes i PostgreSQL.
- Actors gemmes og forbindes til movies.
- Director gemmes og forbindes til movies.
- Genres gemmes og forbindes til movies.
- Eksisterende data oprettes ikke unødvendigt igen.

**Tasks:**
- Opret Movie entity.
- Opret Actor entity.
- Opret Director entity.
- Opret Genre entity.
- Implementer Many-to-Many mellem Movie og Actor.
- Implementer Many-to-One / One-to-Many mellem Movie og Director.
- Implementer Many-to-Many mellem Movie og Genre.
- Implementer DAO- og Service-logik.

**Status:** Færdig


## User Story 3 – CRUD på movies

**User Story:**  
Som klient vil jeg kunne oprette, hente, opdatere og slette movies, så jeg kan administrere data i databasen.

**Acceptance Criteria:**
- En movie kan oprettes.
- Alle movies kan hentes.
- En movie kan findes via database-ID.
- En movie kan findes via TMDb-ID.
- En movie kan opdateres.
- En movie kan slettes.
- Der returneres en fejl, hvis en movie ikke findes.

**Tasks:**
- Implementer create.
- Implementer findAll.
- Implementer findById.
- Implementer findByTmdbId.
- Implementer update.
- Implementer delete.
- Implementer ApiException.
- Test CRUD-funktionaliteten.

**Status:** Færdig


## User Story 4 – Søg efter movies

**User Story:**  
Som klient vil jeg kunne søge efter movies ud fra titel, så jeg nemt kan finde bestemte movies.

**Acceptance Criteria:**
- Klienten kan søge med hele eller dele af en titel.
- Søgningen er case-insensitive.
- Alle movies, hvor søgeteksten indgår i titlen, returneres.

**Tasks:**
- Implementer søgning i MovieDAO.
- Implementer søgning i MovieService.
- Konverter resultatet til MovieResponseDTO.
- Test søgefunktionen.

**Status:** Færdig


## User Story 5 – Find movies efter genre

**User Story:**  
Som klient vil jeg kunne finde movies inden for en bestemt genre, så jeg kan se alle movies i den valgte kategori.

**Acceptance Criteria:**
- Alle genres kan hentes.
- En bestemt genre kan vælges.
- Alle movies tilknyttet genren returneres.

**Tasks:**
- Implementer GenreDAO.
- Implementer GenreService.
- Implementer relationen mellem Movie og Genre.
- Implementer query til movies efter genre.
- Test funktionaliteten.

**Status:** Færdig


## User Story 6 – Movie-statistik

**User Story:**  
Som klient vil jeg kunne se statistik over movies, så jeg kan sammenligne ratings og popularitet.

**Acceptance Criteria:**
- Den gennemsnitlige rating kan beregnes.
- Top 10 højest ratede movies kan hentes.
- Top 10 lavest ratede movies kan hentes.
- Top 10 mest populære movies kan hentes.

**Tasks:**
- Implementer beregning af gennemsnitlig rating.
- Implementer top 10 højeste rating.
- Implementer top 10 laveste rating.
- Implementer top 10 efter popularity.
- Test resultaterne.

**Status:** Færdig


## User Story 7 – Actors

**User Story:**  
Som klient vil jeg kunne hente actors og se deres movies, så jeg kan se hvilke movies en actor har medvirket i.

**Acceptance Criteria:**
- Actors gemmes i databasen.
- Alle actors kan hentes.
- En actor er relateret til sine movies.
- Movies for en bestemt actor kan findes.

**Tasks:**
- Opret Actor entity.
- Opret ActorDAO.
- Opret ActorService.
- Opret ActorResponseDTO.
- Implementer Movie-Actor relationen.
- Implementer søgning efter movies for en actor.

**Status:** Færdig


## User Story 8 – Directors

**User Story:**  
Som klient vil jeg kunne hente directors og se deres movies, så jeg kan se hvilke movies en director har instrueret.

**Acceptance Criteria:**
- Directors gemmes i databasen.
- Alle directors kan hentes.
- En director er relateret til sine movies.
- Movies for en bestemt director kan findes.

**Tasks:**
- Opret Director entity.
- Opret DirectorDAO.
- Opret DirectorService.
- Opret DirectorResponseDTO.
- Implementer Movie-Director relationen.
- Implementer søgning efter movies for en director.

**Status:** Færdig


# Bonus User Stories

## Bonus 1 – Synkronisering med TMDb

**User Story:**  
Som klient vil jeg kunne synkronisere databasen med TMDb, så databasen kan opdateres med de aktuelle movies.

**Acceptance Criteria:**
- Nye movies fra TMDb kan tilføjes.
- Eksisterende movies oprettes ikke igen.
- Movies, som ikke længere findes i TMDb-resultatet, kan fjernes.

**Tasks:**
- Hent aktuelle movies fra TMDb.
- Sammenlign TMDb-data med databasen.
- Tilføj nye movies.
- Fjern movies, der ikke længere findes i resultatet.

**Status:** Færdig


## Bonus 2 – Parallel fetching

**User Story:**  
Som klient vil jeg kunne hente flere TMDb-sider parallelt, så importen kan udføres hurtigere.

**Acceptance Criteria:**
- Flere sider kan hentes samtidigt.
- Der bruges ExecutorService.
- Der bruges Future.
- Thread pool er begrænset til 4 threads.
- De samme threads genbruges til de resterende sider.

**Tasks:**
- Opret FixedThreadPool med 4 threads.
- Opret Callable-tasks til TMDb requests.
- Gem resultaterne i Future.
- Saml resultaterne efter fetching.
- Luk ExecutorService korrekt.

**Status:**  Færdig


# Screencast

I screencasten gennemgår jeg kort projektets kode og struktur, viser at programmet kører og viser data i PostgreSQL-databasen.

**Link til screencast:**  

https://github.com/SanielaDhimitri/sp1-movie-repository

# SP1 - Movie Repository

Backend project for 3rd semester Datamatiker.

The project fetches Danish movie data from the TMDb REST API and stores the data in a PostgreSQL database using JPA/Hibernate.

The application works with movies, actors, directors and genres.

---

## Project Architecture

The project is divided into layers:

text
TMDb REST API
      ↓
   ApiReader
      ↓
     DTO
      ↓
 Service Layer
      ↓
   DAO Layer
      ↓
JPA / Hibernate
      ↓
 PostgreSQL

### ApiReader
`ApiReader` communicates with the TMDb REST API using `HttpClient`.

It fetches JSON data for:

- Movies
- Movie details
- Credits (actors and crew)
- Genres

Jackson `ObjectMapper` is used to convert JSON into DTOs.

### DTO

TMDb DTOs are used for data received from the TMDb API.

Examples:

- `MovieDTO`
- `MovieDetailsDTO`
- `ActorDTO`
- `CrewDTO`
- `CreditsDTO`
- `GenreDTO`
- `MovieResultDTO`
- `GenreResultDTO`

Response DTOs are used when data is returned from the Service Layer.

Examples:

- `MovieResponseDTO`
- `ActorResponseDTO`
- `DirectorResponseDTO`
- `GenreResponseDTO`

### Service Layer

The Service Layer contains the application logic and communicates with the DAO Layer.

Services:

- `MovieService`
- `ActorService`
- `DirectorService`
- `GenreService`

The services convert between DTOs and Entities and handle errors when data cannot be found.

### DAO Layer

The DAO Layer communicates with the database through JPA.

DAO classes:

- `GenericDAO`
- `MovieDAO`
- `ActorDAO`
- `DirectorDAO`
- `GenreDAO`

`GenericDAO` contains common CRUD functionality, while the other DAO classes contain entity-specific database queries.

---

## Entity Relationships

The database contains four main entities:

```text
Actor
  * 
  |
  | Many-to-Many
  |
  *
Movie
  *
  |
  | Many-to-One
  |
  1
Director

Movie
  *
  |
  | Many-to-Many
  |
  *
Genre
```

### Movie - Actor

A movie can have many actors and an actor can participate in many movies.

Relationship:

`ManyToMany`

Join table:

`movie_actor`

### Movie - Director

A movie has a director and a director can direct multiple movies.

Relationship:

`ManyToOne / OneToMany`

### Movie - Genre

A movie can have multiple genres and a genre can contain multiple movies.

Relationship:

`ManyToMany`

Join table:

`movie_genre`

---

## Database Constraints

The entities use database constraints to improve data integrity.

Examples:

- Database IDs are generated automatically.
- TMDb IDs are `unique`.
- TMDb IDs cannot be `null`.
- Names and movie titles cannot be `null`.
- Names and titles have a maximum length of 255 characters.

---

## Danish Movie Import

The application uses the TMDb `/discover/movie` endpoint to fetch movies originating from Denmark.

The date range is calculated dynamically:

```java
LocalDate today = LocalDate.now();
LocalDate fiveYearsAgo = today.minusYears(5);
```

The API request uses:

```text
with_origin_country=DK
primary_release_date.gte=fiveYearsAgo
primary_release_date.lte=today
```

The imported movie data is stored in the local PostgreSQL database.

The import is intended as a once-only operation. After the movies have been stored, the backend reads the data from its own database instead of synchronizing continuously with TMDb.

The database currently contains approximately 1500 Danish movies from the last five years.

---

## Backend Functionality

The backend supports:

- Store movies in the database
- Retrieve all movies
- Retrieve a movie by database ID
- Retrieve a movie by TMDb ID
- Create a movie
- Update a movie
- Delete a movie
- Retrieve actors
- Retrieve directors
- Retrieve genres
- List movies within a particular genre
- Search movies by title
- Case-insensitive title search
- Calculate average movie rating
- Get top 10 highest rated movies
- Get top 10 lowest rated movies
- Get top 10 most popular movies

---

## Error Handling

The project uses a custom `ApiException`.

For example, if a movie does not exist in the database:

```java
throw new ApiException(
        404,
        "Movie with id " + id + " was not found in the database."
);
```

Similar error handling is implemented for actors, directors and genres.

---

## API Key

The TMDb API key is stored as an environment variable and is not written directly in the source code.

```java
String apiKey = System.getenv("API_KEY");
```

The environment variable must be configured in IntelliJ before running the application.

The API key should never be pushed to GitHub.

---

## Technologies

- Java
- JPA
- Hibernate
- PostgreSQL
- Docker
- Jackson
- Java HttpClient
- TMDb REST API
- Maven
- JUnit
- Testcontainers
- Lombok

---

## Testing

The Service Layer is tested with JUnit and Testcontainers.

Tests cover functionality such as:

- Create
- Read
- Update
- Delete
- Error handling
- Search
- Movie statistics
- Genre queries

The tests use a separate PostgreSQL test database through Testcontainers.

---

## Data Flow

Example when importing a movie:

text
TMDb API
   ↓
JSON
   ↓
ApiReader + Jackson
   ↓
MovieDTO / CreditsDTO / MovieDetailsDTO
   ↓
MovieService
   ↓
Movie / Actor / Director / Genre Entities
   ↓
DAO
   ↓
PostgreSQL

When retrieving data:

text
PostgreSQL
   ↓
DAO
   ↓
Entity
   ↓
Service
   ↓
ResponseDTO
   ↓
Main / Client


---

## Project Purpose

The purpose of this project is to demonstrate how a Java backend can integrate with an external REST API, convert JSON data into DTOs using Jackson, store relational data in PostgreSQL using JPA/Hibernate, and structure the application using Service and DAO layers.
---

## Projektets funktionalitet

Projektet er en backend-applikation, der henter danske film fra TMDb API og gemmer data i en PostgreSQL-database ved hjælp af JPA/Hibernate.

Backend-løsningen indeholder følgende funktionalitet:

- Henter danske film fra de sidste 5 år fra TMDb API.
- Konverterer JSON-data til DTO'er med Jackson.
- Gemmer movies, actors, directors og genres i databasen.
- Bruger separate DTO'er og entities.
- Bruger Service Layer til forretningslogik og DTO/Entity-konvertering.
- Bruger DAO Layer til kommunikation med databasen via JPA.
- Understøtter CRUD-operationer for movies.
- Kan hente alle movies, actors, directors og genres.
- Kan søge efter movies via titel.
- Kan finde alle movies inden for en bestemt genre.
- Beregner den gennemsnitlige rating.
- Finder top 10 højeste ratede movies.
- Finder top 10 laveste ratede movies.
- Finder top 10 mest populære movies.
- Håndterer fejl med ApiException.
- Funktionaliteten er testet med JUnit og Testcontainers.


## Bonusfunktionalitet

Projektet implementerer også bonusfunktionalitet:

- Finder alle movies for en bestemt actor.
- Finder alle movies for en bestemt director.
- Kan hente data fra TMDb igen og synkronisere databasen med de aktuelle movies.
- Henter flere TMDb-sider parallelt med Future og ExecutorService.
- Parallel fetching er begrænset til 4 threads.


## Resultater

### Import fra TMDb

Ved den seneste import blev der hentet:

- 1519 danske movies fra de sidste 5 år.
- Data blev hentet fra 76 sider fra TMDb API.
- JSON-data blev konverteret til DTO'er med Jackson.
- Movies, actors, directors og genres blev gemt i PostgreSQL-databasen.


### Movies i databasen

Efter importen indeholdt databasen:

- 1519 movies.

Eksempel på en movie hentet fra databasen:

- Movie: Zip Wire
- Release date: 2026-08-31
- Rating: 7.6
- Popularity: 1.3345
- Director: Howard J. Ford
- Genres: Action, Thriller


### Søgning efter movies

Backend kan søge efter movies ud fra en del af titlen.

Søgningen er case-insensitive og returnerer alle movies, hvor søgeteksten indgår i titlen.


### Rating

Backend kan beregne den gennemsnitlige rating for alle movies i databasen.

Derudover kan backend hente:

- Top 10 højest ratede movies.
- Top 10 lavest ratede movies.
- Top 10 mest populære movies.


### Actors

Actors bliver gemt i databasen og er relateret til deres movies gennem en Many-to-Many relation.

Backend kan hente alle actors og vise de movies, som den enkelte actor har medvirket i.


### Directors

Directors bliver gemt i databasen og er relateret til movies gennem en One-to-Many / Many-to-One relation.

Backend kan hente alle directors og vise de movies, som den enkelte director har instrueret.


### Genres

Genres bliver gemt i databasen og er relateret til movies gennem en Many-to-Many relation.

Backend kan hente alle genres og finde alle movies inden for en bestemt genre.


## Bonusresultater

### Bonus 1 – Movies for en bestemt actor

Backend kan finde en bestemt actor og returnere en liste over de movies, som personen har medvirket i.


### Bonus 2 – Movies for en bestemt director

Backend kan finde en bestemt director og returnere en liste over de movies, som personen har instrueret.


### Bonus 3 – Synkronisering med TMDb

Backend kan hente data fra TMDb igen og sammenligne TMDb-data med de movies, der allerede findes i databasen.

- Nye movies kan tilføjes til databasen.
- Movies, som ikke længere findes i TMDb-resultatet, kan fjernes fra databasen.
- Eksisterende movies bliver ikke oprettet igen.


### Bonus 4 – Parallel fetching

TMDb-siderne kan hentes parallelt ved hjælp af:

- ExecutorService
- Future
- FixedThreadPool

Thread poolen er begrænset til 4 threads.

Ved testen kunne flere sider derfor hentes parallelt, eksempelvis:

Henter side 1 | Thread: pool-1-thread-1  
Henter side 2 | Thread: pool-1-thread-2  
Henter side 3 | Thread: pool-1-thread-3  
Henter side 4 | Thread: pool-1-thread-4

De samme 4 threads bliver derefter genbrugt til de resterende sider.


## Tests

Projektet er testet med JUnit og Testcontainers.

Testene kontrollerer blandt andet:

- Create
- Read
- Update
- Delete
- Find by ID
- Find by TMDb ID
- Service Layer
- Error handling
- Entity relationships

Testene er gennemført successfully.