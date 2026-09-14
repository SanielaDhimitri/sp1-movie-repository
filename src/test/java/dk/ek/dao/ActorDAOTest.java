package dk.ek.dao;

import dk.ek.config.HibernateTestConfig;
import dk.ek.entity.Actor;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ActorDAOTest {

    private static EntityManagerFactory emf;
    private ActorDAO actorDAO;

    @BeforeAll
    static void setUpAll() {
        emf = HibernateTestConfig.getEntityManagerFactory();
    }

    @BeforeEach
    void setUp() {
        actorDAO = new ActorDAO(emf);
    }

    // CREATE + READ
    @Test
    void shouldCreateAndFindActor() {

        Actor actor = new Actor(
                900001L,
                "Test Actor"
        );

        actorDAO.create(actor);

        assertNotNull(actor.getId());

        Actor found = actorDAO.findById(actor.getId());

        assertNotNull(found);
        assertEquals("Test Actor", found.getName());
    }

    // FIND ALL
    @Test
    void shouldFindAllActors() {

        Actor actor = new Actor(
                900002L,
                "Find All Actor"
        );

        actorDAO.create(actor);

        List<Actor> actors = actorDAO.findAll();

        assertFalse(actors.isEmpty());
    }

    // UPDATE
    @Test
    void shouldUpdateActor() {

        Actor actor = new Actor(
                900003L,
                "Old Actor Name"
        );

        actorDAO.create(actor);

        actor.setName("New Actor Name");

        actorDAO.update(actor);

        Actor updated = actorDAO.findById(actor.getId());

        assertEquals(
                "New Actor Name",
                updated.getName()
        );
    }

    // DELETE
    @Test
    void shouldDeleteActor() {

        Actor actor = new Actor(
                900004L,
                "Delete Actor"
        );

        actorDAO.create(actor);

        Long id = actor.getId();

        actorDAO.delete(id);

        Actor deleted = actorDAO.findById(id);

        assertNull(deleted);
    }

    // FIND BY TMDB ID
    @Test
    void shouldFindActorByTmdbId() {

        Actor actor = new Actor(
                900005L,
                "TMDb Actor"
        );

        actorDAO.create(actor);

        Actor found = actorDAO.findByTmdbId(900005L);

        assertNotNull(found);
        assertEquals("TMDb Actor", found.getName());
    }

    @AfterAll
    static void tearDownAll() {
        if (emf != null) {
            emf.close();
        }
    }
}