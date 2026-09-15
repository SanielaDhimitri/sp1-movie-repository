package dk.ek.service;

import dk.ek.exceptions.ApiException;


import dk.ek.dao.ActorDAO;
import dk.ek.dao.DirectorDAO;
import dk.ek.dao.GenreDAO;
import dk.ek.dao.MovieDAO;

import dk.ek.dto.response.MovieResponseDTO;
import dk.ek.dto.tmdb.ActorDTO;
import dk.ek.dto.tmdb.CreditsDTO;
import dk.ek.dto.tmdb.CrewDTO;
import dk.ek.dto.tmdb.GenreDTO;
import dk.ek.dto.tmdb.MovieDetailsDTO;
import dk.ek.dto.tmdb.MovieDTO;

import dk.ek.entity.Actor;
import dk.ek.entity.Director;
import dk.ek.entity.Genre;
import dk.ek.entity.Movie;

import java.time.LocalDate;
import java.util.List;

public class MovieService {

    private final MovieDAO movieDAO;
    private final ActorDAO actorDAO;
    private final DirectorDAO directorDAO;
    private final GenreDAO genreDAO;

    public MovieService(
            MovieDAO movieDAO,
            ActorDAO actorDAO,
            DirectorDAO directorDAO,
            GenreDAO genreDAO
    ) {
        this.movieDAO = movieDAO;
        this.actorDAO = actorDAO;
        this.directorDAO = directorDAO;
        this.genreDAO = genreDAO;
    }


    // CREATE
// Modtager DTO fra Main/controller,
// konverterer DTO til Entity og gemmer Movie i databasen.
    public MovieResponseDTO createMovie(MovieDTO movieDTO) {

        // Tjekker om movie allerede findes i databasen.
        Movie existingMovie =
                movieDAO.findByTmdbId((long) movieDTO.id());

        if (existingMovie != null) {
            return toDTO(existingMovie);
        }

        // Konverterer MovieDTO til Movie Entity.
        Movie movie = toEntity(movieDTO);

        // Gemmer Movie Entity i databasen.
        movieDAO.create(movie);

        // Konverterer Entity til ResponseDTO og returnerer den.
        return toDTO(movie);
    }

    // CREATE MOVIE WITH RELATIONSHIPS
// Modtager DTO'er fra TMDb og bygger en komplet Movie.
// Actors, director og genres findes/oprettes gennem DAO-laget.
    public MovieResponseDTO createMovieWithRelations(
            MovieDTO movieDTO,
            CreditsDTO creditsDTO,
            MovieDetailsDTO detailsDTO
    ) {

        // Tjekker om movie allerede findes.
        Movie existingMovie =
                movieDAO.findByTmdbId((long) movieDTO.id());

        if (existingMovie != null) {
            return toDTO(existingMovie);
        }

        // MovieDTO -> Movie Entity
        Movie movie = toEntity(movieDTO);


        // ---------- ACTORS ----------

        for (ActorDTO actorDTO : creditsDTO.cast()) {

            Actor actor =
                    actorDAO.findByTmdbId(actorDTO.id());

            if (actor == null) {
                actor = new Actor(
                        actorDTO.id(),
                        actorDTO.name()
                );

                actorDAO.create(actor);
            }

            movie.getActors().add(actor);
        }


        // ---------- DIRECTOR ----------

        for (CrewDTO crewDTO : creditsDTO.crew()) {

            if ("Director".equals(crewDTO.job())) {

                Director director =
                        directorDAO.findByTmdbId(crewDTO.id());

                if (director == null) {
                    director = new Director(
                            crewDTO.id(),
                            crewDTO.name()
                    );

                    directorDAO.create(director);
                }

                movie.setDirector(director);

                break;
            }
        }


        // ---------- GENRES ----------

        for (GenreDTO genreDTO : detailsDTO.genres()) {

            Genre genre =
                    genreDAO.findByTmdbId(genreDTO.id());

            if (genre == null) {
                genre = new Genre(
                        genreDTO.id(),
                        genreDTO.name()
                );

                genreDAO.create(genre);
            }

            movie.getGenres().add(genre);
        }


        // Gemmer den komplette Movie med relationships.
        movieDAO.create(movie);

        return toDTO(movie);
    }

    // READ - Find movie by database ID
    public MovieResponseDTO getMovieById(Long id) {

        Movie movie = movieDAO.findById(id);

        if (movie == null) {
            throw new ApiException(
                    404,
                    "Movie with id " + id + " was not found in the database."
            );
        }

        return toDTO(movie);
    }


    // READ - Find all movies
    public List<MovieResponseDTO> getAllMovies() {

        List<Movie> movies = movieDAO.findAll();

        return movies.stream()
                .map(this::toDTO)
                .toList();
    }

    // READ - Find movie by TMDb ID
    public MovieResponseDTO getMovieByTmdbId(Long tmdbId) {

        Movie movie = movieDAO.findByTmdbId(tmdbId);

        if (movie == null) {
            throw new ApiException(
                    404,
                    "Movie with TMDb id " + tmdbId + " was not found in the database."
            );
        }

        return toDTO(movie);
    }


    // UPDATE
    public MovieResponseDTO updateMovie(Long id, MovieDTO movieDTO) {

        Movie movie = movieDAO.findById(id);

        if (movie == null) {
            throw new ApiException(
                    404,
                    "Movie with id " + id + " was not found in the database."
            );
        }

        movie.setTmdbId((long) movieDTO.id());
        movie.setTitle(movieDTO.title());

        if (movieDTO.releaseDate() != null
                && !movieDTO.releaseDate().isBlank()) {

            movie.setReleaseDate(
                    LocalDate.parse(movieDTO.releaseDate())
            );
        }

        movie.setRating(movieDTO.rating());
        movie.setPopularity(movieDTO.popularity());

        Movie updatedMovie = movieDAO.update(movie);

        return toDTO(updatedMovie);
    }


    // DELETE
    public boolean deleteMovie(Long id) {

        Movie movie = movieDAO.findById(id);

        if (movie == null) {
            throw new ApiException(
                    404,
                    "Movie with id " + id + " was not found in the database."
            );
        }

        movieDAO.delete(id);

        return true;
    }

    // DTO -> ENTITY
    private Movie toEntity(MovieDTO movieDTO) {

        LocalDate releaseDate = null;

        if (movieDTO.releaseDate() != null
                && !movieDTO.releaseDate().isBlank()) {

            releaseDate = LocalDate.parse(
                    movieDTO.releaseDate()
            );
        }

        return new Movie(
                (long) movieDTO.id(),
                movieDTO.title(),
                releaseDate,
                movieDTO.rating(),
                movieDTO.popularity()
        );
    }


    // ENTITY -> RESPONSE DTO
    private MovieResponseDTO toDTO(Movie movie) {

        List<String> actors = movie.getActors()
                .stream()
                .map(Actor::getName)
                .toList();

        String director = movie.getDirector() != null
                ? movie.getDirector().getName()
                : null;

        List<String> genres = movie.getGenres()
                .stream()
                .map(Genre::getName)
                .toList();

        return new MovieResponseDTO(
                movie.getId(),
                movie.getTmdbId(),
                movie.getTitle(),
                movie.getReleaseDate(),
                movie.getRating(),
                movie.getPopularity(),
                actors,
                director,
                genres
        );
    }
    // SEARCH - Finder movies via title
    public List<MovieResponseDTO> searchByTitle(String title) {

        return movieDAO.searchByTitle(title)
                .stream()
                .map(this::toDTO)
                .toList();
    }
    // Gennemsnitlig rating
    public Double getAverageRating() {
        return movieDAO.getAverageRating();
    }


    // Top 10 højeste rating
    public List<MovieResponseDTO> getTop10HighestRated() {

        return movieDAO.getTop10HighestRated()
                .stream()
                .map(this::toDTO)
                .toList();
    }


    // Top 10 laveste rating
    public List<MovieResponseDTO> getTop10LowestRated() {

        return movieDAO.getTop10LowestRated()
                .stream()
                .map(this::toDTO)
                .toList();
    }


    // Top 10 mest populære
    public List<MovieResponseDTO> getTop10MostPopular() {

        return movieDAO.getTop10MostPopular()
                .stream()
                .map(this::toDTO)
                .toList();
    }
    // READ - Finder alle movies inden for en bestemt genre
    public List<MovieResponseDTO> getMoviesByGenre(Long genreId) {

        Genre genre = genreDAO.findById(genreId);

        if (genre == null) {
            throw new ApiException(
                    404,
                    "Genre with id " + genreId + " was not found in the database."
            );
        }

        return movieDAO.findMoviesByGenre(genreId)
                .stream()
                .map(this::toDTO)
                .toList();
    }
}

