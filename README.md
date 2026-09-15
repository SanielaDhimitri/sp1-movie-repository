# SP1 - Movie Repository

Backend project for 3rd semester Datamatiker.

The project fetches Danish movie data from the TMDb REST API and stores the data in a PostgreSQL database using JPA/Hibernate.

The application works with movies, actors, directors and genres.

---

## Project Architecture

The project is divided into layers:

```text
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
```

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

```text
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
```

When retrieving data:

```text
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
```

---

## Project Purpose

The purpose of the project is to demonstrate how a Java backend can integrate with an external REST API, convert JSON data into DTOs, store relational data using JPA/Hibernate and provide backend functionality through a Service and DAO architecture.