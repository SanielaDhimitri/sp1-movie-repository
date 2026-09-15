package dk.ek.service;

import dk.ek.config.HibernateTestConfig;
import dk.ek.dao.GenreDAO;
import dk.ek.dto.response.GenreResponseDTO;
import dk.ek.dto.tmdb.GenreDTO;
import dk.ek.entity.Genre;
import dk.ek.exceptions.ApiException;
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


    // =========================================================
    // READ BY DATABASE ID
    // =========================================================

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


    // ERROR - DATABASE ID DOES NOT EXIST
    @Test
    void shouldThrowExceptionWhenGenreDoesNotExist() {

        ApiException exception = assertThrows(
                ApiException.class,
                () -> genreService.getGenreById(999999999L)
        );

        assertEquals(404, exception.getCode());
    }


    // =========================================================
    // READ ALL
    // =========================================================

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


    // =========================================================
    // READ BY TMDB ID
    // =========================================================

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


    // ERROR - TMDB ID DOES NOT EXIST
    @Test
    void shouldThrowExceptionWhenGenreTmdbIdDoesNotExist() {

        ApiException exception = assertThrows(
                ApiException.class,
                () -> genreService.getGenreByTmdbId(999999999L)
        );

        assertEquals(404, exception.getCode());
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    void shouldUpdateGenre() {

        Genre genre = new Genre(
                300004L,
                "Old Genre Name"
        );

        genreDAO.create(genre);

        GenreDTO updatedDTO = new GenreDTO(
                300004L,
                "New Genre Name"
        );

        GenreResponseDTO result =
                genreService.updateGenre(
                        genre.getId(),
                        updatedDTO
                );

        assertNotNull(result);
        assertEquals("New Genre Name", result.name());

        Genre updatedGenre =
                genreDAO.findById(genre.getId());

        assertEquals(
                "New Genre Name",
                updatedGenre.getName()
        );
    }


    // ERROR - GENRE DOES NOT EXIST
    @Test
    void shouldThrowExceptionWhenUpdatingGenreDoesNotExist() {

        GenreDTO genreDTO = new GenreDTO(
                999999999L,
                "Unknown Genre"
        );

        ApiException exception = assertThrows(
                ApiException.class,
                () -> genreService.updateGenre(
                        999999999L,
                        genreDTO
                )
        );

        assertEquals(404, exception.getCode());
    }


    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void shouldDeleteGenre() {

        Genre genre = new Genre(
                300005L,
                "Delete Genre"
        );

        genreDAO.create(genre);

        Long id = genre.getId();

        boolean deleted =
                genreService.deleteGenre(id);

        assertTrue(deleted);
        assertNull(genreDAO.findById(id));
    }


    // ERROR - GENRE DOES NOT EXIST
    @Test
    void shouldThrowExceptionWhenDeletingGenreDoesNotExist() {

        ApiException exception = assertThrows(
                ApiException.class,
                () -> genreService.deleteGenre(999999999L)
        );

        assertEquals(404, exception.getCode());
    }


    @AfterAll
    static void tearDownAll() {

        if (emf != null) {
            emf.close();
        }
    }
}