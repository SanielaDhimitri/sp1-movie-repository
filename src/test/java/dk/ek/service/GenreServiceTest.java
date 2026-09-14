package dk.ek.service;

import dk.ek.config.HibernateTestConfig;
import dk.ek.dao.GenreDAO;
import dk.ek.dto.response.GenreResponseDTO;
import dk.ek.entity.Genre;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GenreServiceTest {

    private static EntityManagerFactory emf;

    private GenreDAO genreDAO;
    private GenreService genreService;

    @BeforeAll
    static void setUpAll() {
        emf = HibernateTestConfig.getEntityManagerFactory();
    }

    @BeforeEach
    void setUp() {
        genreDAO = new GenreDAO(emf);
        genreService = new GenreService(genreDAO);
    }

    @Test
    void shouldGetGenreById() {

        Genre genre = new Genre(
                300001L,
                "Test Genre"
        );

        genreDAO.create(genre);

        GenreResponseDTO result =
                genreService.getGenreById(genre.getId());

        assertNotNull(result);
        assertEquals("Test Genre", result.name());
    }

    @Test
    void shouldGetAllGenres() {

        Genre genre = new Genre(
                300002L,
                "All Genres Test"
        );

        genreDAO.create(genre);

        List<GenreResponseDTO> genres =
                genreService.getAllGenres();

        assertNotNull(genres);
        assertFalse(genres.isEmpty());
    }

    @Test
    void shouldGetGenreByTmdbId() {

        Genre genre = new Genre(
                300003L,
                "TMDb Genre Test"
        );

        genreDAO.create(genre);

        GenreResponseDTO result =
                genreService.getGenreByTmdbId(300003L);

        assertNotNull(result);
        assertEquals("TMDb Genre Test", result.name());
    }

    @Test
    void shouldReturnNullWhenGenreDoesNotExist() {

        GenreResponseDTO result =
                genreService.getGenreById(999999999L);

        assertNull(result);
    }

    @AfterAll
    static void tearDownAll() {
        if (emf != null) {
            emf.close();
        }
    }
}