# SP1 – Movie Repository

Backend project for 3rd semester Datamatiker.

The application fetches Danish movies from the TMDb REST API and stores
the data in a PostgreSQL database using JPA/Hibernate.

The project works with:

- Movies
- Actors
- Directors
- Genres

The backend is structured using DTO, Service and DAO layers.


---

# User Stories

## User Story 1 – Hent og gem danske movies fra TMDb

### User Story
Som klient vil jeg kunne hente danske movies fra de sidste 5 år fra TMDb,
så de kan gemmes og bruges i backend-systemet.

### Acceptance Criteria
- Der hentes danske movies fra de sidste 5 år.
- Data hentes fra TMDb REST API.
- JSON-data konverteres til DTO'er med Jackson.
- Movie details og credits hentes fra TMDb.
- Movies, actors, directors og genres gemmes i PostgreSQL.

### Tasks
- Opret ApiReader med HttpClient.
- Opret forbindelse til TMDb API.
- Opret DTO'er til TMDb-data.
- Parse JSON med Jackson ObjectMapper.
- Hent movie details og credits.
- Konverter DTO'er til entities.
- Gem data via Service Layer og DAO Layer.

**Status: Færdig**


---

## User Story 2 – Se alle movies

### User Story
Som klient vil jeg kunne hente en liste over alle movies fra databasen,
så jeg kan se de movies, der er gemt i systemet.

### Acceptance Criteria
- Alle movies kan hentes fra PostgreSQL.
- Movies returneres som MovieResponseDTO.
- Listen indeholder alle movies i databasen.

### Tasks
- Implementer findAll i MovieDAO.
- Implementer getAllMovies i MovieService.
- Konverter Movie entities til MovieResponseDTO.
- Test funktionaliteten.

**Status: Færdig**


---

## User Story 3 – Actors og directors

### User Story
Som klient vil jeg kunne se alle actors og directors,
der har været en del af de gemte movies.

### Acceptance Criteria
- Actors gemmes i databasen.
- Directors gemmes i databasen.
- Alle actors kan hentes.
- Alle directors kan hentes.
- Actors og directors er relateret korrekt til movies.

### Relationships
- Movie ↔ Actor: Many-to-Many
- Movie → Director: Many-to-One
- Director → Movie: One-to-Many

### Tasks
- Opret Actor og Director entities.
- Opret ActorDAO og DirectorDAO.
- Opret ActorService og DirectorService.
- Implementer Movie-Actor relation.
- Implementer Movie-Director relation.
- Hent actors og director fra TMDb credits.

**Status: Færdig**


---

## User Story 4 – Genres

### User Story
Som klient vil jeg kunne se alle genres og finde alle movies
inden for en bestemt genre.

### Acceptance Criteria
- Alle genres kan hentes.
- En bestemt genre kan vælges.
- Alle movies tilknyttet genren returneres.
- Movie og Genre har en Many-to-Many relation.

### Tasks
- Opret Genre entity.
- Opret GenreDAO.
- Opret GenreService.
- Implementer Movie-Genre relation.
- Implementer query til movies efter genre.
- Test funktionaliteten.

**Status: Færdig**


---

## User Story 5 – CRUD på movies

### User Story
Som klient vil jeg kunne oprette, hente, opdatere og slette movies,
så jeg kan administrere data i databasen.

### Acceptance Criteria
- En movie kan oprettes.
- Alle movies kan hentes.
- En movie kan findes via database-ID.
- En movie kan findes via TMDb-ID.
- En movie kan opdateres.
- Title og release date kan opdateres.
- En movie kan slettes.
- Der håndteres fejl, hvis en movie ikke findes.

### Tasks
- Implementer create.
- Implementer findAll.
- Implementer findById.
- Implementer findByTmdbId.
- Implementer update.
- Implementer delete.
- Implementer ApiException.
- Test CRUD-funktionaliteten.

**Status: Færdig**


---

## User Story 6 – Søg efter movies

### User Story
Som klient vil jeg kunne søge efter movies ud fra titel,
så jeg nemt kan finde bestemte movies.

### Acceptance Criteria
- Der kan søges med hele eller dele af en titel.
- Søgningen er case-insensitive.
- Alle movies, hvor søgeteksten indgår i titlen, returneres.

### Tasks
- Implementer searchByTitle i MovieDAO.
- Implementer searchByTitle i MovieService.
- Konverter resultatet til MovieResponseDTO.
- Test søgefunktionen.

**Status: Færdig**


---

## User Story 7 – Movie-statistik

### User Story
Som klient vil jeg kunne se statistik over movies,
så jeg kan sammenligne ratings og popularitet.

### Acceptance Criteria
- Den gennemsnitlige rating kan beregnes.
- Top 10 højest ratede movies kan hentes.
- Top 10 lavest ratede movies kan hentes.
- Top 10 mest populære movies kan hentes.

### Tasks
- Implementer gennemsnitlig rating.
- Implementer top 10 højeste rating.
- Implementer top 10 laveste rating.
- Implementer top 10 efter popularity.
- Test resultaterne.

**Status: Færdig**


---

# Bonus User Stories

## Bonus 1 – Movies for en bestemt actor

### User Story
Som klient vil jeg kunne se alle movies,
som en bestemt actor har medvirket i.

### Acceptance Criteria
- En actor kan findes.
- Actorens movies kan hentes.
- Movie-Actor relationen bruges til at finde resultatet.

**Status: Færdig**


---

## Bonus 2 – Movies for en bestemt director

### User Story
Som klient vil jeg kunne se alle movies,
som en bestemt director har instrueret.

### Acceptance Criteria
- En director kan findes.
- Directorens movies kan hentes.
- Movie-Director relationen bruges til at finde resultatet.

**Status: Færdig**


---

## Bonus 3 – Synkronisering med TMDb

### User Story
Som klient vil jeg kunne hente data fra TMDb igen og synkronisere
databasen med de aktuelle movies.

### Acceptance Criteria
- Nye movies kan tilføjes.
- Eksisterende movies oprettes ikke igen.
- Movies, som ikke længere findes i TMDb-resultatet, kan fjernes.

### Implementation
TMDb movie IDs gemmes under importen:

    tmdbMovieIds.add((long) movieDTO.id());

Efter importen synkroniseres databasen:

    movieService.deleteMoviesNotInTmdb(tmdbMovieIds);

**Status: Færdig**


---

## Bonus 4 – Parallel fetching

### User Story
Som klient vil jeg kunne hente flere TMDb-sider parallelt,
så importen kan udføres hurtigere.

### Acceptance Criteria
- Flere TMDb-pages hentes parallelt.
- Der bruges ExecutorService.
- Der bruges Future.
- Thread pool er begrænset til 4 threads.
- De samme threads genbruges.
- ExecutorService lukkes korrekt.

### Implementation

    ExecutorService executor =
            Executors.newFixedThreadPool(4);

Resultaterne gemmes som:

    Future<MovieResultDTO>

og hentes med:

    future.get();

Til sidst:

    executor.shutdown();

**Status: Færdig**


---

# Project Architecture

Projektet er opdelt i flere lag:

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


## ApiReader

ApiReader kommunikerer med TMDb REST API ved hjælp af Java HttpClient.

Den henter:

- Movies
- Movie details
- Credits
- Actors
- Directors
- Genres

Jackson ObjectMapper konverterer JSON-data til DTO'er.


## DTO

TMDb DTO'er bruges til data fra TMDb:

- MovieDTO
- MovieDetailsDTO
- ActorDTO
- CrewDTO
- CreditsResultDTO
- GenreDTO
- MovieResultDTO
- GenreResultDTO

Response DTO'er bruges til data fra backend:

- MovieResponseDTO
- ActorResponseDTO
- DirectorResponseDTO
- GenreResponseDTO


## Service Layer

Service Layer indeholder applikationslogikken.

Services:

- MovieService
- ActorService
- DirectorService
- GenreService

Service Layer:

- kommunikerer med DAO Layer
- konverterer Entities til ResponseDTO'er
- håndterer applikationslogik
- håndterer fejl


## DAO Layer

DAO Layer kommunikerer med PostgreSQL gennem JPA/Hibernate.

DAO classes:

- GenericDAO
- MovieDAO
- ActorDAO
- DirectorDAO
- GenreDAO

GenericDAO indeholder fælles CRUD-funktionalitet.

De øvrige DAO-klasser indeholder entity-specifikke queries.


---

# Entity Relationships

## Movie – Actor

En movie kan have mange actors,
og en actor kan medvirke i mange movies.

Relationship:

    Many-to-Many

Join table:

    movie_actor


## Movie – Director

En movie har en director,
og en director kan instruere flere movies.

Relationship:

    Movie → Director: Many-to-One
    Director → Movie: One-to-Many


## Movie – Genre

En movie kan have flere genres,
og en genre kan indeholde flere movies.

Relationship:

    Many-to-Many

Join table:

    movie_genre


---

# Danish Movie Import

Applikationen bruger TMDb `/discover/movie` til at hente
movies med oprindelsesland Danmark.

Datoerne beregnes dynamisk:

    LocalDate today = LocalDate.now();
    LocalDate fiveYearsAgo = today.minusYears(5);

TMDb-requestet bruger:

    with_origin_country=DK
    primary_release_date.gte=fiveYearsAgo
    primary_release_date.lte=today

For hver movie hentes også:

    Movie
       ↓
    Credits
       ↓
    Actors + Director

og:

    Movie
       ↓
    Details
       ↓
    Genres

Derefter gemmes movie og relationerne:

    movieService.createMovieWithRelations(
        movieDTO,
        creditsResultDTO,
        movieDetailsDTO
    );


---

# Data Flow

## Import fra TMDb

    TMDb API
        ↓
       JSON
        ↓
    ApiReader
        ↓
      Jackson
        ↓
       DTO
        ↓
    MovieService
        ↓
      Entity
        ↓
       DAO
        ↓
    PostgreSQL


## Data fra database til klient

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
Client


---

# Error Handling

Projektet bruger en custom ApiException.

Eksempel:

    throw new ApiException(
        404,
        "Movie with id " + id + " was not found in the database."
    );

ApiException bruges også ved fejl for actors, directors og genres.


---

# API Key

TMDb API key gemmes som en environment variable:

    String apiKey = System.getenv("API_KEY");

API key ligger derfor ikke direkte i source code
og skal ikke pushes til GitHub.


---

# Testing

Projektet er testet med:

- JUnit
- Testcontainers
- PostgreSQL test database

Testene dækker blandt andet:

- Create
- Read
- Update
- Delete
- Find by ID
- Find by TMDb ID
- Search
- Movie statistics
- Genre queries
- Error handling
- Entity relationships


---

# Results

Ved den seneste import blev der hentet:

- 1519 danske movies
- 76 TMDb pages
- Movies fra de sidste 5 år

JSON-data blev konverteret til DTO'er med Jackson,
og movies, actors, directors og genres blev gemt i PostgreSQL.

Databasen indeholdt efter importen:

    1519 movies


---

# Technologies

- Java
- Maven
- TMDb REST API
- Java HttpClient
- Jackson
- JPA
- Hibernate
- PostgreSQL
- Docker
- JUnit
- Testcontainers
- Lombok


---

# Screencast

I screencasten gennemgår jeg:

1. Projektets struktur
2. TMDb integration
3. DTO'er
4. Entities og relationships
5. DAO Layer
6. Service Layer
7. PostgreSQL database
8. Search og statistics
9. Bonusfunktionalitet
10. Tests


**GitHub repository / screencast:**

https://github.com/SanielaDhimitri/sp1-movie-repository