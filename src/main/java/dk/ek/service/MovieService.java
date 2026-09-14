package dk.ek.service;

import dk.ek.dao.MovieDAO;
import dk.ek.dto.tmdb.MovieDTO;
import dk.ek.dto.response.MovieResponseDTO;
import dk.ek.entity.Movie;

import java.time.LocalDate;
import java.util.List;

public class MovieService {

    private final MovieDAO movieDAO;

    public MovieService(MovieDAO movieDAO) {
        this.movieDAO = movieDAO;
    }


    // CREATE
    // Modtager DTO fra Main/controller,
    // konverterer DTO til Entity og gemmer Movie i databasen.
    public MovieResponseDTO createMovie(MovieDTO movieDTO) {

        Movie existingMovie =
                movieDAO.findByTmdbId((long) movieDTO.id());

        if (existingMovie != null) {
            return toDTO(existingMovie);
        }

        LocalDate releaseDate = null;

        if (movieDTO.releaseDate() != null
                && !movieDTO.releaseDate().isBlank()) {

            releaseDate =
                    LocalDate.parse(movieDTO.releaseDate());
        }

        Movie movie = new Movie(
                (long) movieDTO.id(),
                movieDTO.title(),
                releaseDate,
                movieDTO.rating(),
                movieDTO.popularity()
        );

        movieDAO.create(movie);

        return toDTO(movie);
    }


    // READ - Find movie by database ID
    public MovieResponseDTO getMovieById(Long id) {

        Movie movie = movieDAO.findById(id);

        if (movie == null) {
            return null;
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
            return null;
        }

        return toDTO(movie);
    }


    // UPDATE
    public MovieResponseDTO updateMovie(Long id, MovieDTO movieDTO) {

        Movie movie = movieDAO.findById(id);

        if (movie == null) {
            return null;
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
            return false;
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

        return new MovieResponseDTO(
                movie.getId(),
                movie.getTmdbId(),
                movie.getTitle(),
                movie.getReleaseDate(),
                movie.getRating(),
                movie.getPopularity()
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
}

