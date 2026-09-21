package dk.ek.service;

import dk.ek.config.HibernateTestConfig;
import dk.ek.dao.ActorDAO;
import dk.ek.dao.DirectorDAO;
import dk.ek.dao.GenreDAO;
import dk.ek.dao.MovieDAO;
import dk.ek.dto.dbresponse.MovieResponseDTO;
import dk.ek.dto.tmdb.*;
import dk.ek.entity.Genre;
import dk.ek.entity.Movie;
import dk.ek.exceptions.ApiException;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import dk.ek.entity.Actor;
import dk.ek.entity.Director;

class MovieServiceTest {

    private static EntityManagerFactory emf;

    private MovieDAO movieDAO;
    private ActorDAO actorDAO;
    private DirectorDAO directorDAO;
    private GenreDAO genreDAO;

    private MovieService movieService;


    @BeforeAll
    static void setUpAll() {
        emf = HibernateTestConfig.getEntityManagerFactory();
    }


    @BeforeEach
    void setUp() {

        movieDAO = new MovieDAO(emf);
        actorDAO = new ActorDAO(emf);
        directorDAO = new DirectorDAO(emf);
        genreDAO = new GenreDAO(emf);

        movieService = new MovieService(
                movieDAO,
                actorDAO,
                directorDAO,
                genreDAO
        );
    }


    // =========================================================
    // CREATE
    // =========================================================

    @Test
    void shouldCreateMovie() {

        MovieDTO movieDTO = new MovieDTO(
                600010,
                "Create Movie Test",
                "Test overview",
                "2026-09-15",
                8.5,
                50.0
        );

        MovieResponseDTO result =
                movieService.createMovie(movieDTO);

        assertNotNull(result);
        assertNotNull(result.id());
        assertEquals(600010L, result.tmdbId());
        assertEquals("Create Movie Test", result.title());

        // Kontrollerer at filmen faktisk blev gemt i databasen.
        Movie savedMovie =
                movieDAO.findByTmdbId(600010L);

        assertNotNull(savedMovie);
    }


    // =========================================================
    // CREATE WITH RELATIONSHIPS
    // =========================================================

    @Test
    void shouldCreateMovieWithRelations() {

        MovieDTO movieDTO = new MovieDTO(
                600011,
                "Movie With Relations",
                "Test overview",
                "2026-09-15",
                9.0,
                70.0
        );

        ActorDTO actorDTO =
                new ActorDTO(
                        610001L,
                        "Test Actor"
                );

        CrewDTO directorDTO =
                new CrewDTO(
                        620001L,
                        "Test Director",
                        "Director"
                );

        GenreDTO genreDTO =
                new GenreDTO(
                        630001L,
                        "Test Genre"
                );

        CreditsResultDTO creditsResultDTO =
                new CreditsResultDTO(
                        List.of(actorDTO),
                        List.of(directorDTO)
                );

        MovieDetailsDTO detailsDTO =
                new MovieDetailsDTO(
                        600011L,
                        "Movie With Relations",
                        List.of(genreDTO)
                );

        MovieResponseDTO result =
                movieService.createMovieWithRelations(
                        movieDTO,
                        creditsResultDTO,
                        detailsDTO
                );

        assertNotNull(result);
        assertEquals(
                "Movie With Relations",
                result.title()
        );

        // Kontrollerer relationerne gennem DAO'erne.
        assertNotNull(
                actorDAO.findByTmdbId(610001L)
        );

        assertNotNull(
                directorDAO.findByTmdbId(620001L)
        );

        assertNotNull(
                genreDAO.findByTmdbId(630001L)
        );

        Movie savedMovie =
                movieDAO.findByTmdbId(600011L);

        assertNotNull(savedMovie);
    }


    // =========================================================
    // READ BY DATABASE ID
    // =========================================================

    @Test
    void shouldGetMovieById() {

        Movie movie = new Movie(
                600001L,
                "Service Test Movie",
                LocalDate.of(2026, 9, 14),
                8.5,
                50.0
        );

        movieDAO.create(movie);

        MovieResponseDTO result =
                movieService.getMovieById(movie.getId());

        assertNotNull(result);
        assertEquals(
                "Service Test Movie",
                result.title()
        );
    }


    // ERROR - DATABASE ID DOES NOT EXIST
    @Test
    void shouldThrowExceptionWhenMovieDoesNotExist() {

        ApiException exception = assertThrows(
                ApiException.class,
                () -> movieService.getMovieById(999999999L)
        );

        assertEquals(404, exception.getCode());
    }


    // =========================================================
    // READ ALL
    // =========================================================

    @Test
    void shouldGetAllMovies() {

        Movie movie = new Movie(
                600002L,
                "All Movies Test",
                LocalDate.of(2026, 1, 1),
                7.5,
                40.0
        );

        movieDAO.create(movie);

        List<MovieResponseDTO> movies =
                movieService.getAllMovies();

        assertNotNull(movies);
        assertFalse(movies.isEmpty());
    }


    // =========================================================
    // READ BY TMDB ID
    // =========================================================

    @Test
    void shouldGetMovieByTmdbId() {

        Movie movie = new Movie(
                600003L,
                "TMDb Service Test",
                LocalDate.of(2026, 1, 1),
                8.0,
                45.0
        );

        movieDAO.create(movie);

        MovieResponseDTO result =
                movieService.getMovieByTmdbId(600003L);

        assertNotNull(result);
        assertEquals(
                "TMDb Service Test",
                result.title()
        );
    }


    // ERROR - TMDB ID DOES NOT EXIST
    @Test
    void shouldThrowExceptionWhenMovieTmdbIdDoesNotExist() {

        ApiException exception = assertThrows(
                ApiException.class,
                () -> movieService.getMovieByTmdbId(999999999L)
        );

        assertEquals(404, exception.getCode());
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    void shouldUpdateMovie() {

        Movie movie = new Movie(
                600004L,
                "Old Title",
                LocalDate.of(2026, 1, 1),
                5.0,
                20.0
        );

        movieDAO.create(movie);

        MovieDTO updatedDTO = new MovieDTO(
                600004,
                "New Title",
                "Updated overview",
                "2026-02-01",
                9.0,
                80.0
        );

        MovieResponseDTO result =
                movieService.updateMovie(
                        movie.getId(),
                        updatedDTO
                );

        assertNotNull(result);
        assertEquals("New Title", result.title());
        assertEquals(
                LocalDate.of(2026, 2, 1),
                result.releaseDate()
        );
        assertEquals(9.0, result.rating());
        assertEquals(80.0, result.popularity());

        Movie updatedMovie =
                movieDAO.findById(movie.getId());

        assertEquals(
                "New Title",
                updatedMovie.getTitle()
        );
    }


    // ERROR - MOVIE DOES NOT EXIST
    @Test
    void shouldThrowExceptionWhenUpdatingMovieDoesNotExist() {

        MovieDTO movieDTO = new MovieDTO(
                999999999,
                "Unknown Movie",
                "Test overview",
                "2026-09-15",
                5.0,
                10.0
        );

        ApiException exception = assertThrows(
                ApiException.class,
                () -> movieService.updateMovie(
                        999999999L,
                        movieDTO
                )
        );

        assertEquals(404, exception.getCode());
    }


    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void shouldDeleteMovie() {

        Movie movie = new Movie(
                600005L,
                "Delete Service Test",
                LocalDate.of(2026, 1, 1),
                6.0,
                30.0
        );

        movieDAO.create(movie);

        Long id = movie.getId();

        boolean deleted =
                movieService.deleteMovie(id);

        assertTrue(deleted);
        assertNull(movieDAO.findById(id));
    }


    // ERROR - MOVIE DOES NOT EXIST
    @Test
    void shouldThrowExceptionWhenDeletingMovieDoesNotExist() {

        ApiException exception = assertThrows(
                ApiException.class,
                () -> movieService.deleteMovie(999999999L)
        );

        assertEquals(404, exception.getCode());
    }


    // =========================================================
    // SEARCH BY TITLE
    // =========================================================

    @Test
    void shouldSearchByTitle() {

        Movie movie = new Movie(
                600006L,
                "Godzilla Service Test",
                LocalDate.of(2026, 1, 1),
                8.0,
                50.0
        );

        movieDAO.create(movie);

        List<MovieResponseDTO> result =
                movieService.searchByTitle("godzilla");

        assertFalse(result.isEmpty());

        assertTrue(
                result.stream()
                        .anyMatch(m ->
                                m.title().equals(
                                        "Godzilla Service Test"
                                )
                        )
        );
    }


    // =========================================================
    // AVERAGE RATING
    // =========================================================

    @Test
    void shouldCalculateAverageRating() {

        Movie movie1 = new Movie(
                600020L,
                "Average Test 1",
                LocalDate.of(2026, 1, 1),
                6.0,
                10.0
        );

        Movie movie2 = new Movie(
                600021L,
                "Average Test 2",
                LocalDate.of(2026, 1, 1),
                8.0,
                10.0
        );

        movieDAO.create(movie1);
        movieDAO.create(movie2);

        Double average =
                movieService.getAverageRating();

        assertNotNull(average);
        assertTrue(average > 0);
    }


    // =========================================================
    // TOP 10 HIGHEST RATED
    // =========================================================

    @Test
    void shouldGetTop10HighestRated() {

        List<MovieResponseDTO> movies =
                movieService.getTop10HighestRated();

        assertNotNull(movies);
        assertTrue(movies.size() <= 10);
    }


    // =========================================================
    // TOP 10 LOWEST RATED
    // =========================================================

    @Test
    void shouldGetTop10LowestRated() {

        List<MovieResponseDTO> movies =
                movieService.getTop10LowestRated();

        assertNotNull(movies);
        assertTrue(movies.size() <= 10);
    }


    // =========================================================
    // TOP 10 MOST POPULAR
    // =========================================================

    @Test
    void shouldGetTop10MostPopular() {

        List<MovieResponseDTO> movies =
                movieService.getTop10MostPopular();

        assertNotNull(movies);
        assertTrue(movies.size() <= 10);
    }



    // =========================================================
// GET MOVIES BY GENRE
// =========================================================

    @Test
    void shouldGetMoviesByGenre() {

        // Opretter genre
        Genre genre = new Genre(
                700001L,
                "Action Test"
        );
        genreDAO.create(genre);

        // Opretter movie
        Movie movie = new Movie(
                800001L,
                "Action Movie Test",
                LocalDate.of(2025, 1, 1),
                7.5,
                50.0
        );

        // Tilføjer genre til movie
        movie.getGenres().add(genre);

        // Gemmer movie
        movieDAO.create(movie);

        // Henter movies gennem Service Layer
        List<MovieResponseDTO> result =
                movieService.getMoviesByGenre(genre.getId());

        assertNotNull(result);
        assertFalse(result.isEmpty());

        assertTrue(
                result.stream()
                        .anyMatch(m ->
                                m.title().equals("Action Movie Test")
                        )
        );
    }


    // ERROR - GENRE DOES NOT EXIST
    @Test
    void shouldThrowExceptionWhenGenreDoesNotExist() {

        ApiException exception = assertThrows(
                ApiException.class,
                () -> movieService.getMoviesByGenre(999999999L)
        );

        assertEquals(404, exception.getCode());
    }

    @Test
    void shouldReturnMovieWithRelationships() {

        Actor actor = new Actor(
                500001L,
                "Test Actor"
        );

        Director director = new Director(
                500002L,
                "Test Director"
        );

        Genre genre = new Genre(
                500003L,
                "Test Genre"
        );

        actorDAO.create(actor);
        directorDAO.create(director);
        genreDAO.create(genre);

        Movie movie = new Movie(
                500004L,
                "Movie With Relationships",
                LocalDate.of(2025, 1, 1),
                8.0,
                100.0
        );

        movie.getActors().add(actor);
        movie.setDirector(director);
        movie.getGenres().add(genre);

        movieDAO.create(movie);

        MovieResponseDTO result =
                movieService.getMovieById(movie.getId());

        assertNotNull(result);

        assertTrue(
                result.actors().contains("Test Actor")
        );

        assertEquals(
                "Test Director",
                result.director()
        );

        assertTrue(
                result.genres().contains("Test Genre")
        );
    }

    // BONUS 3 - Sletter movies som ikke længere findes i TMDb
    @Test
    void shouldDeleteMoviesNotInTmdb() {

        // Opretter en test-movie i databasen
        Movie movie = new Movie(
                999999L,
                "BONUS 3 DELETE TEST",
                LocalDate.of(2025, 1, 1),
                7.0,
                10.0
        );

        movieDAO.create(movie);

        // Listen simulerer de movies, som stadig findes i TMDb.
        // 999999L er ikke med i listen.
        List<Long> tmdbMovieIds = List.of(
                111111L,
                222222L
        );

        // Kalder Bonus 3-metoden
        movieService.deleteMoviesNotInTmdb(tmdbMovieIds);

        // Kontrollerer at test-movie er blevet slettet
        Movie deletedMovie =
                movieDAO.findById(movie.getId());

        assertNull(deletedMovie);
    }
    // BONUS 3 - Beholder movie hvis den stadig findes i TMDb
    @Test
    void shouldNotDeleteMovieIfItStillExistsInTmdb() {

        // Opretter en test-movie i databasen
        Movie movie = new Movie(
                888888L,
                "BONUS 3 KEEP TEST",
                LocalDate.of(2025, 1, 1),
                8.0,
                20.0
        );

        movieDAO.create(movie);

        // Movie med TMDb-id 888888 findes stadig i TMDb-listen
        List<Long> tmdbMovieIds = List.of(
                888888L
        );

        // Kalder Bonus 3-metoden
        movieService.deleteMoviesNotInTmdb(tmdbMovieIds);

        // Henter movie igen fra databasen
        Movie existingMovie =
                movieDAO.findById(movie.getId());

        // Kontrollerer at movie IKKE blev slettet
        assertNotNull(existingMovie);

        assertEquals(
                "BONUS 3 KEEP TEST",
                existingMovie.getTitle()
        );
    }
    // =========================================================
// CLEANUP
// =========================================================

    @AfterAll
    static void tearDownAll() {
        if (emf != null) {
            emf.close();
        }
    }
}