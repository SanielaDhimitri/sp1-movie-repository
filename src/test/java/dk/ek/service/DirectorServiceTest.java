package dk.ek.service;

import dk.ek.config.HibernateTestConfig;
import dk.ek.dao.DirectorDAO;
import dk.ek.dto.response.DirectorResponseDTO;
import dk.ek.entity.Director;
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

    @Test
    void shouldReturnNullWhenDirectorDoesNotExist() {

        DirectorResponseDTO result =
                directorService.getDirectorById(999999999L);

        assertNull(result);
    }

    @AfterAll
    static void tearDownAll() {
        if (emf != null) {
            emf.close();
        }
    }
}