package dk.ek.service;

import dk.ek.config.HibernateTestConfig;
import dk.ek.dao.DirectorDAO;
import dk.ek.dto.response.DirectorResponseDTO;
import dk.ek.dto.tmdb.CrewDTO;
import dk.ek.entity.Director;
import dk.ek.exceptions.ApiException;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DirectorServiceTest {

    private static EntityManagerFactory emf;

    private DirectorDAO directorDAO;
    private DirectorService directorService;


    @BeforeAll
    static void setUpAll() {
        emf = HibernateTestConfig.getEntityManagerFactory();
    }


    @BeforeEach
    void setUp() {
        directorDAO = new DirectorDAO(emf);
        directorService = new DirectorService(directorDAO);
    }


    // =========================================================
    // READ BY DATABASE ID
    // =========================================================

    @Test
    void shouldGetDirectorById() {

        Director director = new Director(
                400001L,
                "Test Director"
        );

        directorDAO.create(director);

        DirectorResponseDTO result =
                directorService.getDirectorById(director.getId());

        assertNotNull(result);
        assertEquals("Test Director", result.name());
    }


    // ERROR - DATABASE ID DOES NOT EXIST
    @Test
    void shouldThrowExceptionWhenDirectorDoesNotExist() {

        ApiException exception = assertThrows(
                ApiException.class,
                () -> directorService.getDirectorById(999999999L)
        );

        assertEquals(404, exception.getCode());
    }


    // =========================================================
    // READ ALL
    // =========================================================

    @Test
    void shouldGetAllDirectors() {

        Director director = new Director(
                400002L,
                "All Directors Test"
        );

        directorDAO.create(director);

        List<DirectorResponseDTO> directors =
                directorService.getAllDirectors();

        assertNotNull(directors);
        assertFalse(directors.isEmpty());
    }


    // =========================================================
    // READ BY TMDB ID
    // =========================================================

    @Test
    void shouldGetDirectorByTmdbId() {

        Director director = new Director(
                400003L,
                "TMDb Director Test"
        );

        directorDAO.create(director);

        DirectorResponseDTO result =
                directorService.getDirectorByTmdbId(400003L);

        assertNotNull(result);
        assertEquals("TMDb Director Test", result.name());
    }


    // ERROR - TMDB ID DOES NOT EXIST
    @Test
    void shouldThrowExceptionWhenDirectorTmdbIdDoesNotExist() {

        ApiException exception = assertThrows(
                ApiException.class,
                () -> directorService.getDirectorByTmdbId(999999999L)
        );

        assertEquals(404, exception.getCode());
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    void shouldUpdateDirector() {

        Director director = new Director(
                400004L,
                "Old Director Name"
        );

        directorDAO.create(director);

        CrewDTO updatedDTO = new CrewDTO(
                400004L,
                "New Director Name",
                "Director"
        );

        DirectorResponseDTO result =
                directorService.updateDirector(
                        director.getId(),
                        updatedDTO
                );

        assertNotNull(result);
        assertEquals("New Director Name", result.name());
    }


    // ERROR - DIRECTOR DOES NOT EXIST
    @Test
    void shouldThrowExceptionWhenUpdatingDirectorDoesNotExist() {

        CrewDTO directorDTO = new CrewDTO(
                999999999L,
                "Unknown Director",
                "Director"
        );

        ApiException exception = assertThrows(
                ApiException.class,
                () -> directorService.updateDirector(
                        999999999L,
                        directorDTO
                )
        );

        assertEquals(404, exception.getCode());
    }


    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void shouldDeleteDirector() {

        Director director = new Director(
                400005L,
                "Delete Director"
        );

        directorDAO.create(director);

        Long id = director.getId();

        boolean deleted =
                directorService.deleteDirector(id);

        assertTrue(deleted);
        assertNull(directorDAO.findById(id));
    }


    // ERROR - DIRECTOR DOES NOT EXIST
    @Test
    void shouldThrowExceptionWhenDeletingDirectorDoesNotExist() {

        ApiException exception = assertThrows(
                ApiException.class,
                () -> directorService.deleteDirector(999999999L)
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