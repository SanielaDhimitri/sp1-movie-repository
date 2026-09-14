package dk.ek.dao;

import dk.ek.config.HibernateTestConfig;
import dk.ek.entity.Movie;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MovieDAOTest {

    private static EntityManagerFactory emf;
    private MovieDAO movieDAO;

    @BeforeAll
    static void setUpAll() {
        emf = HibernateTestConfig.getEntityManagerFactory();
    }

    @BeforeEach
    void setUp() {
        movieDAO = new MovieDAO(emf);
    }


    // CREATE + READ
    @Test
    void shouldCreateAndFindMovie() {

        Movie movie = new Movie(
                999001L,
                "Test Movie",
                LocalDate.of(2026, 9, 14),
                8.5,
                50.0
        );

        movieDAO.create(movie);

        assertNotNull(movie.getId());

        Movie found = movieDAO.findById(movie.getId());

        assertNotNull(found);
        assertEquals("Test Movie", found.getTitle());
    }


    // FIND ALL
    @Test
    void shouldFindAllMovies() {

        Movie movie = new Movie(
                999002L,
                "Find All Test",
                LocalDate.of(2026, 1, 1),
                7.5,
                30.0
        );

        movieDAO.create(movie);

        List<Movie> movies = movieDAO.findAll();

        assertFalse(movies.isEmpty());
    }


    // UPDATE
    @Test
    void shouldUpdateMovie() {

        Movie movie = new Movie(
                999003L,
                "Old Title",
                LocalDate.of(2026, 1, 1),
                6.0,
                20.0
        );

        movieDAO.create(movie);

        movie.setTitle("New Title");

        movieDAO.update(movie);

        Movie updated = movieDAO.findById(movie.getId());

        assertEquals("New Title", updated.getTitle());
    }


    // DELETE
    @Test
    void shouldDeleteMovie() {

        Movie movie = new Movie(
                999004L,
                "Delete Test",
                LocalDate.of(2026, 1, 1),
                5.0,
                10.0
        );

        movieDAO.create(movie);

        Long id = movie.getId();

        movieDAO.delete(id);

        Movie deleted = movieDAO.findById(id);

        assertNull(deleted);
    }


    // FIND BY TMDB ID
    @Test
    void shouldFindMovieByTmdbId() {

        Movie movie = new Movie(
                999005L,
                "TMDb Test",
                LocalDate.of(2026, 1, 1),
                7.0,
                40.0
        );

        movieDAO.create(movie);

        Movie found = movieDAO.findByTmdbId(999005L);

        assertNotNull(found);
        assertEquals("TMDb Test", found.getTitle());
    }


    // SEARCH BY TITLE
    @Test
    void shouldSearchMovieByTitle() {

        Movie movie = new Movie(
                999006L,
                "Godland Test",
                LocalDate.of(2026, 1, 1),
                7.5,
                50.0
        );

        movieDAO.create(movie);

        List<Movie> result =
                movieDAO.searchByTitle("godland");

        assertFalse(result.isEmpty());

        assertTrue(
                result.stream()
                        .anyMatch(movieResult ->
                                movieResult.getTitle()
                                        .equals("Godland Test"))
        );
    }


    @AfterAll
    static void tearDownAll() {

        if (emf != null) {
            emf.close();
        }
    }
}