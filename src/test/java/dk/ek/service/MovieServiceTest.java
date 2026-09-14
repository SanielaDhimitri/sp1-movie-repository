package dk.ek.service;

import dk.ek.config.HibernateTestConfig;
import dk.ek.dao.MovieDAO;
import dk.ek.dto.response.MovieResponseDTO;
import dk.ek.entity.Movie;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MovieServiceTest {

    private static EntityManagerFactory emf;

    private MovieDAO movieDAO;
    private MovieService movieService;

    @BeforeAll
    static void setUpAll() {
        emf = HibernateTestConfig.getEntityManagerFactory();
    }

    @BeforeEach
    void setUp() {
        movieDAO = new MovieDAO(emf);
        movieService = new MovieService(movieDAO);
    }

    // GET MOVIE BY ID
    @Test
    void shouldGetMovieById() {

        Movie movie = new Movie(
                600001L,
                "Service Test Movie",
                LocalDate.of(2026, 9, 14),
                8.5,
                50.0
        );

        movieDAO.create(movie);

        MovieResponseDTO result =
                movieService.getMovieById(movie.getId());

        assertNotNull(result);
        assertEquals("Service Test Movie", result.title());
    }

    // GET ALL MOVIES
    @Test
    void shouldGetAllMovies() {

        Movie movie = new Movie(
                600002L,
                "All Movies Test",
                LocalDate.of(2026, 1, 1),
                7.5,
                40.0
        );

        movieDAO.create(movie);

        List<MovieResponseDTO> movies =
                movieService.getAllMovies();

        assertNotNull(movies);
        assertFalse(movies.isEmpty());
    }

    // GET MOVIE BY TMDB ID
    @Test
    void shouldGetMovieByTmdbId() {

        Movie movie = new Movie(
                600003L,
                "TMDb Service Test",
                LocalDate.of(2026, 1, 1),
                8.0,
                45.0
        );

        movieDAO.create(movie);

        MovieResponseDTO result =
                movieService.getMovieByTmdbId(600003L);

        assertNotNull(result);
        assertEquals("TMDb Service Test", result.title());
    }

    // MOVIE DOES NOT EXIST
    @Test
    void shouldReturnNullWhenMovieDoesNotExist() {

        MovieResponseDTO result =
                movieService.getMovieById(999999999L);

        assertNull(result);
    }

    @AfterAll
    static void tearDownAll() {
        if (emf != null) {
            emf.close();
        }
    }
}