package dk.ek.service;

import dk.ek.config.HibernateTestConfig;
import dk.ek.dao.ActorDAO;
import dk.ek.dto.response.ActorResponseDTO;
import dk.ek.entity.Actor;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ActorServiceTest {

    private static EntityManagerFactory emf;

    private ActorDAO actorDAO;
    private ActorService actorService;

    @BeforeAll
    static void setUpAll() {
        emf = HibernateTestConfig.getEntityManagerFactory();
    }

    @BeforeEach
    void setUp() {
        actorDAO = new ActorDAO(emf);
        actorService = new ActorService(actorDAO);
    }

    // GET ACTOR BY ID
    @Test
    void shouldGetActorById() {

        Actor actor = new Actor(
                500001L,
                "Test Actor"
        );

        actorDAO.create(actor);

        ActorResponseDTO result =
                actorService.getActorById(actor.getId());

        assertNotNull(result);
        assertEquals("Test Actor", result.name());
    }

    // GET ALL ACTORS
    @Test
    void shouldGetAllActors() {

        Actor actor = new Actor(
                500002L,
                "All Actors Test"
        );

        actorDAO.create(actor);

        List<ActorResponseDTO> actors =
                actorService.getAllActors();

        assertNotNull(actors);
        assertFalse(actors.isEmpty());
    }

    // GET ACTOR BY TMDB ID
    @Test
    void shouldGetActorByTmdbId() {

        Actor actor = new Actor(
                500003L,
                "TMDb Actor Test"
        );

        actorDAO.create(actor);

        ActorResponseDTO result =
                actorService.getActorByTmdbId(500003L);

        assertNotNull(result);
        assertEquals("TMDb Actor Test", result.name());
    }

    // ACTOR DOES NOT EXIST
    @Test
    void shouldReturnNullWhenActorDoesNotExist() {

        ActorResponseDTO result =
                actorService.getActorById(999999999L);

        assertNull(result);
    }

    @AfterAll
    static void tearDownAll() {
        if (emf != null) {
            emf.close();
        }
    }
}