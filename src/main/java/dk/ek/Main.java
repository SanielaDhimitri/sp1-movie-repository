package dk.ek;

import dk.ek.api.ApiReader;
import dk.ek.config.HibernateConfig;
import dk.ek.dao.ActorDAO;
import dk.ek.dao.DirectorDAO;
import dk.ek.dao.GenreDAO;
import dk.ek.dao.MovieDAO;
import dk.ek.dto.tmdb.*;
import dk.ek.service.ActorService;
import dk.ek.service.DirectorService;
import dk.ek.service.GenreService;
import dk.ek.service.MovieService;
import jakarta.persistence.EntityManagerFactory;


public class Main {

    public static void main(String[] args) {

        // ---------- SETUP ----------

        //  forbindelse til db (gennem Hibernate).
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

        // Henter TMDb API key (fra environment variable.)
        String apiKey = System.getenv("API_KEY");

        // Stopper, hvis API key mangler.
        if (apiKey == null || apiKey.isBlank()) {throw new IllegalStateException(
                    "API_KEY environment variable mangler."
            );
        }

        // Opretter ApiReader, som kommunikerer med TMDb API.
        ApiReader apiReader = new ApiReader(apiKey);


        // ----------fetch data: GENRES FRA TMDb REST API + JSON → DTO me Jackson
        // (gemmes ikke i db.for Bruges ikke service+DAO)

        // TMDb main/endpoint til movie genres.
        String genreUrl = "https://api.themoviedb.org/3/genre/movie/list";

        // Henter genres fra TMDb som JSON.
        String genreJson = apiReader.getGenres(genreUrl);

        // JSON til DTO med Jackson.
        GenreResultDTO genreResultDTO = apiReader.convertGenresFromJson(genreJson);

        // Viser alle genres fra TMDb.
        for (GenreDTO genreDTO : genreResultDTO.genres()) {
            System.out.println(
                    "Genre: " + genreDTO.id() + " - " + genreDTO.name()
            );
        }


        // ---------- DAO LAYER ----------

        // Opretter DAO'er, som kommunikerer med databasen.
        MovieDAO movieDAO = new MovieDAO(emf);
        DirectorDAO directorDAO = new DirectorDAO(emf);
        ActorDAO actorDAO = new ActorDAO(emf);
        GenreDAO genreDAO = new GenreDAO(emf);


        // ---------- SERVICE LAYER ----------

        // Opretter services, som bruger DAO-laget fra main.
        MovieService movieService = new MovieService(movieDAO, actorDAO, directorDAO, genreDAO);
        ActorService actorService = new ActorService(actorDAO);
        DirectorService directorService = new DirectorService(directorDAO);
        GenreService genreService = new GenreService(genreDAO);


        // ==========================================================
        // GODZILLA EKSEMPEL
        // Henter Godzilla-data fra TMDb og konverterer JSON til DTO'er.
       // Til sidst hentes Godzilla fra databasen gennem MovieService.
        // Main opretter ikke Entities og gemmer ikke direkte via DAO.
        // ==========================================================

        String creditsUrl = "https://api.themoviedb.org/3/movie";

        // Henter credits for Godzilla (TMDb ID 1678).
        String creditsJson = apiReader.getMovieCredits(creditsUrl, 1678);

        CreditsDTO creditsDTO = apiReader.convertCreditsFromJson(creditsJson);

        // Henter details for Godzilla.
        String detailsJson = apiReader.getMovieDetails(creditsUrl, 1678);

        MovieDetailsDTO movieDetailsDTO = apiReader.convertMovieDetailsFromJson(detailsJson);

        System.out.println("\n========== GODZILLA ==========");
        System.out.println("Title: " + movieDetailsDTO.title());

        System.out.println("\nGenres:");
        movieDetailsDTO.genres().forEach(
                genre -> System.out.println("- " + genre.name())
        );


        System.out.println("\nActors:");
        creditsDTO.cast().stream()
                .limit(10)
                .forEach(actor ->
                        System.out.println("- " + actor.name())
                );

        creditsDTO.crew().stream()
                .filter(crew -> "Director".equals(crew.job()))
                .findFirst()
                .ifPresent(director ->
                        System.out.println(
                                "\nDirector: " + director.name()
                        )
                );

        // READ gennem Service Layer.
        // Godzilla findes allerede i databasen efter importen.
        System.out.println("\nGodzilla fra database:");
        System.out.println(
                movieService.getMovieByTmdbId(1678L)
        );


        // ==========================================================
        // BACKEND DEMO - READ, SEARCH OG STATISTIK
        // ==========================================================
        System.out.println("\n========== DATABASE ==========");

        System.out.println(
                "Antal movies: " + movieService.getAllMovies().size()
        );

        System.out.println("\n========== ALL MOVIES ==========");

        movieService.getAllMovies()
                .forEach(System.out::println);

        System.out.println("\n========== SEARCH: GODZILLA ==========");
        movieService.searchByTitle("Godzilla")
                .forEach(System.out::println);

        System.out.println("\n========== AVERAGE RATING ==========");
        System.out.println(
                movieService.getAverageRating()
        );

        System.out.println("\n========== TOP 10 HIGHEST RATED ==========");
        movieService.getTop10HighestRated()
                .forEach(System.out::println);

        System.out.println("\n========== TOP 10 LOWEST RATED ==========");
        movieService.getTop10LowestRated()
                .forEach(System.out::println);

        System.out.println("\n========== TOP 10 MOST POPULAR ==========");
        movieService.getTop10MostPopular()
                .forEach(System.out::println);

// ==========================================================
// SERVICE LAYER DEMO
// Henter data fra databasen gennem de forskellige services.
// ==========================================================

        System.out.println("\n========== ACTORS ==========");
        actorService.getAllActors()
                .stream()
                .limit(10)
                .forEach(System.out::println);

        System.out.println("\n========== DIRECTORS ==========");
        directorService.getAllDirectors()
                .stream()
                .limit(10)
                .forEach(System.out::println);

        System.out.println("\n========== GENRES ==========");
        genreService.getAllGenres()
                .forEach(System.out::println);



        // ==========================================================
        // DANSKE MOVIES - ONCE-ONLY IMPORT
        //
        // Filmene er allerede importeret til databasen.
        // Sæt IMPORT_DANISH_MOVIES = true KUN hvis databasen
        // skal fyldes igen.
        // ==========================================================
        //once-only operation.
       // boolean IMPORT_DANISH_MOVIES = true;
        boolean IMPORT_DANISH_MOVIES = false;


        String checkUrl = "https://api.themoviedb.org/3/discover/movie";

        String checkJson = apiReader.discoverMovies(checkUrl, 1);

        MovieResultDTO checkResult =
                apiReader.convertFromJson(checkJson);

        System.out.println(
                "Danske filmer af de 5 sidste år: "
                        + checkResult.totalResults()
        );

        if (IMPORT_DANISH_MOVIES) {

            String moviesUrl =
                    "https://api.themoviedb.org/3/discover/movie";

            String firstJson =
                    apiReader.discoverMovies(moviesUrl, 1);

            MovieResultDTO firstPage =
                    apiReader.convertFromJson(firstJson);

            System.out.println(
                    "Total movies: " + firstPage.totalResults()
            );

            System.out.println(
                    "Total pages: " + firstPage.totalPages()
            );

            for (int page = 1;
                 page <= firstPage.totalPages();
                 page++) {

                System.out.println(
                        "\nHenter side "
                                + page
                                + " af "
                                + firstPage.totalPages()
                );

                String pageJson =
                        apiReader.discoverMovies(
                                moviesUrl,
                                page
                        );

                MovieResultDTO pageResult =
                        apiReader.convertFromJson(pageJson);

                for (MovieDTO movieDTO : pageResult.results()) {

                    String danishCreditsJson =
                            apiReader.getMovieCredits(
                                    creditsUrl,
                                    movieDTO.id()
                            );

                    CreditsDTO danishCreditsDTO =
                            apiReader.convertCreditsFromJson(
                                    danishCreditsJson
                            );

                    String danishDetailsJson =
                            apiReader.getMovieDetails(
                                    creditsUrl,
                                    movieDTO.id()
                            );

                    MovieDetailsDTO danishDetailsDTO =
                            apiReader.convertMovieDetailsFromJson(
                                    danishDetailsJson
                            );

                    // Main sender DTO'er til Service Layer.
                    movieService.createMovieWithRelations(
                            movieDTO,
                            danishCreditsDTO,
                            danishDetailsDTO
                    );

                    System.out.println(
                            "Dansk movie: "
                                    + movieDTO.title()
                    );
                }
            }
        }

        // Lukker EntityManagerFactory når programmet er færdigt.
        emf.close();
    }
}

