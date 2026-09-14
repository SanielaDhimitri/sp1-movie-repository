package dk.ek.dao;

import dk.ek.config.HibernateTestConfig;
import dk.ek.entity.Genre;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GenreDAOTest {

    private static EntityManagerFactory emf;
    private GenreDAO genreDAO;

    @BeforeAll
    static void setUpAll() {
        emf = HibernateTestConfig.getEntityManagerFactory();
    }

    @BeforeEach
    void setUp() {
        genreDAO = new GenreDAO(emf);
    }

    // CREATE + READ
    @Test
    void shouldCreateAndFindGenre() {

        Genre genre = new Genre(
                700001L,
                "Test Genre"
        );

        genreDAO.create(genre);

        assertNotNull(genre.getId());

        Genre found = genreDAO.findById(genre.getId());

        assertNotNull(found);
        assertEquals("Test Genre", found.getName());
    }

    // FIND ALL
    @Test
    void shouldFindAllGenres() {

        Genre genre = new Genre(
                700002L,
                "Find All Genre"
        );

        genreDAO.create(genre);

        List<Genre> genres = genreDAO.findAll();

        assertFalse(genres.isEmpty());
    }

    // UPDATE
    @Test
    void shouldUpdateGenre() {

        Genre genre = new Genre(
                700003L,
                "Old Genre"
        );

        genreDAO.create(genre);

        genre.setName("New Genre");

        genreDAO.update(genre);

        Genre updated = genreDAO.findById(genre.getId());

        assertEquals("New Genre", updated.getName());
    }

    // DELETE
    @Test
    void shouldDeleteGenre() {

        Genre genre = new Genre(
                700004L,
                "Delete Genre"
        );

        genreDAO.create(genre);

        Long id = genre.getId();

        genreDAO.delete(id);

        Genre deleted = genreDAO.findById(id);

        assertNull(deleted);
    }

    // FIND BY TMDB ID
    @Test
    void shouldFindGenreByTmdbId() {

        Genre genre = new Genre(
                700005L,
                "TMDb Genre"
        );

        genreDAO.create(genre);

        Genre found = genreDAO.findByTmdbId(700005L);

        assertNotNull(found);
        assertEquals("TMDb Genre", found.getName());
    }

    @AfterAll
    static void tearDownAll() {
        if (emf != null) {
            emf.close();
        }
    }
}