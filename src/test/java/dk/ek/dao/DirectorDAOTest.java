package dk.ek.dao;

import dk.ek.config.HibernateTestConfig;
import dk.ek.entity.Director;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DirectorDAOTest {

    private static EntityManagerFactory emf;
    private DirectorDAO directorDAO;

    @BeforeAll
    static void setUpAll() {
        emf = HibernateTestConfig.getEntityManagerFactory();
    }

    @BeforeEach
    void setUp() {
        directorDAO = new DirectorDAO(emf);
    }

    // CREATE + READ
    @Test
    void shouldCreateAndFindDirector() {

        Director director = new Director(
                800001L,
                "Test Director"
        );

        directorDAO.create(director);

        assertNotNull(director.getId());

        Director found = directorDAO.findById(director.getId());

        assertNotNull(found);
        assertEquals("Test Director", found.getName());
    }

    // FIND ALL
    @Test
    void shouldFindAllDirectors() {

        Director director = new Director(
                800002L,
                "Find All Director"
        );

        directorDAO.create(director);

        List<Director> directors = directorDAO.findAll();

        assertFalse(directors.isEmpty());
    }

    // UPDATE
    @Test
    void shouldUpdateDirector() {

        Director director = new Director(
                800003L,
                "Old Director"
        );

        directorDAO.create(director);

        director.setName("New Director");

        directorDAO.update(director);

        Director updated =
                directorDAO.findById(director.getId());

        assertEquals("New Director", updated.getName());
    }

    // DELETE
    @Test
    void shouldDeleteDirector() {

        Director director = new Director(
                800004L,
                "Delete Director"
        );

        directorDAO.create(director);

        Long id = director.getId();

        directorDAO.delete(id);

        Director deleted = directorDAO.findById(id);

        assertNull(deleted);
    }

    // FIND BY TMDB ID
    @Test
    void shouldFindDirectorByTmdbId() {

        Director director = new Director(
                800005L,
                "TMDb Director"
        );

        directorDAO.create(director);

        Director found =
                directorDAO.findByTmdbId(800005L);

        assertNotNull(found);
        assertEquals("TMDb Director", found.getName());
    }

    @AfterAll
    static void tearDownAll() {
        if (emf != null) {
            emf.close();
        }
    }
}
