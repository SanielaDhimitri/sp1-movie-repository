package dk.ek.service;

import dk.ek.config.HibernateTestConfig;
import dk.ek.dao.ActorDAO;
import dk.ek.dao.MovieDAO;
import dk.ek.dto.response.ActorResponseDTO;
import dk.ek.dto.tmdb.ActorDTO;
import dk.ek.entity.Actor;
import dk.ek.exceptions.ApiException;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import dk.ek.entity.Movie;

import java.time.LocalDate;


class ActorServiceTest {

    private static EntityManagerFactory emf;

    private ActorDAO actorDAO;
    private MovieDAO movieDAO;
    private ActorService actorService;

    @BeforeAll
    static void setUpAll() {
        emf = HibernateTestConfig.getEntityManagerFactory();
    }

    @BeforeEach
    void setUp() {
        actorDAO = new ActorDAO(emf);
        movieDAO = new MovieDAO(emf);
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


    // GET BY ID - ACTOR DOES NOT EXIST
    @Test
    void shouldThrowExceptionWhenActorDoesNotExist() {

        ApiException exception = assertThrows(
                ApiException.class,
                () -> actorService.getActorById(999999999L)
        );

        assertEquals(404, exception.getCode());
    }


    // GET BY TMDB ID - ACTOR DOES NOT EXIST
    @Test
    void shouldThrowExceptionWhenActorTmdbIdDoesNotExist() {

        ApiException exception = assertThrows(
                ApiException.class,
                () -> actorService.getActorByTmdbId(999999999L)
        );

        assertEquals(404, exception.getCode());
    }


    // UPDATE ACTOR
    @Test
    void shouldUpdateActor() {

        Actor actor = new Actor(
                500004L,
                "Old Actor Name"
        );

        actorDAO.create(actor);

        ActorDTO actorDTO = new ActorDTO(
                500004L,
                "New Actor Name"
        );

        ActorResponseDTO result =
                actorService.updateActor(actor.getId(), actorDTO);

        assertNotNull(result);
        assertEquals("New Actor Name", result.name());
    }


    // UPDATE - ACTOR DOES NOT EXIST
    @Test
    void shouldThrowExceptionWhenUpdatingActorDoesNotExist() {

        ActorDTO actorDTO = new ActorDTO(
                999999999L,
                "Unknown Actor"
        );

        ApiException exception = assertThrows(
                ApiException.class,
                () -> actorService.updateActor(
                        999999999L,
                        actorDTO
                )
        );

        assertEquals(404, exception.getCode());
    }


    // DELETE ACTOR
    @Test
    void shouldDeleteActor() {

        Actor actor = new Actor(
                500005L,
                "Delete Actor"
        );

        actorDAO.create(actor);

        Long id = actor.getId();

        boolean deleted =
                actorService.deleteActor(id);

        assertTrue(deleted);
    }


    // DELETE - ACTOR DOES NOT EXIST
    @Test
    void shouldThrowExceptionWhenDeletingActorDoesNotExist() {

        ApiException exception = assertThrows(
                ApiException.class,
                () -> actorService.deleteActor(999999999L)
        );

        assertEquals(404, exception.getCode());
    }

    // BONUS 1 - Finder alle movies for en bestemt actor
    @Test
    void shouldGetMoviesForActor() {

        // Opretter actor
        Actor actor = new Actor(
                500010L,
                "Bonus Actor"
        );

        actorDAO.create(actor);

        // Opretter movie
        Movie movie = new Movie(
                600010L,
                "Bonus Movie",
                LocalDate.of(2025, 5, 10),
                8.0,
                50.0
        );

        // Tilføjer actor til movie
        movie.getActors().add(actor);

        movieDAO.create(movie);

        // Henter actor gennem Service Layer
        ActorResponseDTO result =
                actorService.getActorById(actor.getId());

        // Kontrollerer at actor har filmen
        assertNotNull(result);

        assertTrue(
                result.movies().contains("Bonus Movie")
        );
    }
    @AfterAll
    static void tearDownAll() {
        if (emf != null) {
            emf.close();
        }
    }
}