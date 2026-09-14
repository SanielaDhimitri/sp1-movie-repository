# SP-1 Movie Repository

Backend project for the 3rd semester Data Integration course.

The project fetches Danish movie data from the TMDb API, converts the JSON data to DTOs using Jackson, and stores the data in a PostgreSQL database using JPA/Hibernate.

## Technologies

- Java
- Maven
- JPA / Hibernate
- PostgreSQL
- Jackson
- TMDb API
- JUnit
- Testcontainers
- Java Streams and Lambdas

## Project Structure

The project is divided into different layers:

- `api` - communicates with the TMDb API
- `dto.tmdb` - represents data received from TMDb
- `dto.response` - represents data returned from the service layer
- `entity` - JPA entities stored in the database
- `dao` - handles database operations
- `service` - contains business logic and converts between Entities and DTOs
- `config` - Hibernate and database configuration
- `exceptions` - exception handling
- `test` - DAO and Service tests using JUnit and Testcontainers

## Entities

The database contains four main entities:

- Movie
- Actor
- Director
- Genre

Movies are connected to actors, directors and genres through JPA relationships.

## Functionality

The backend can:

- Fetch Danish movies from the TMDb API
- Store movies in PostgreSQL
- Store actors, directors and genres
- List all movies
- List actors and their movies
- List directors and their movies
- List genres and movies within each genre
- Create, read, update and delete movies
- Search movies by title (case-insensitive)
- Calculate the average movie rating
- Show the top 10 highest rated movies
- Show the top 10 lowest rated movies
- Show the top 10 most popular movies

## TMDb Integration

`ApiReader` communicates with the TMDb API using Java `HttpClient`.

The JSON responses are converted to DTOs with Jackson before the data is converted to entities and stored in the database.

Example flow:

```text
TMDb API
   ↓
JSON
   ↓
DTO
   ↓
Entity
   ↓
DAO
   ↓
PostgreSQL
```

## Testing

The DAO and Service layers are tested using:

- JUnit
- Testcontainers
- PostgreSQL container

This allows the tests to run against a real temporary PostgreSQL database without using the development database.

## API Key

The TMDb API key is stored as an environment variable:

```java
String apiKey = System.getenv("API_KEY");
```

The API key is not included in the GitHub repository.

## Author

Saniela Dhimitri