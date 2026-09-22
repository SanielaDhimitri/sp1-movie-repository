package dk.ek;

import dk.ek.api.ApiReader;
import dk.ek.config.HibernateConfig;
import dk.ek.dao.ActorDAO;
import dk.ek.dao.DirectorDAO;
import dk.ek.dao.GenreDAO;
import dk.ek.dao.MovieDAO;
import dk.ek.dto.dbresponse.ActorResponseDTO;
import dk.ek.dto.dbresponse.DirectorResponseDTO;
import dk.ek.dto.dbresponse.MovieResponseDTO;
import dk.ek.dto.tmdb.*;
import dk.ek.service.ActorService;
import dk.ek.service.DirectorService;
import dk.ek.service.GenreService;
import dk.ek.service.MovieService;
import jakarta.persistence.EntityManagerFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import java.util.ArrayList;
import java.util.List;


public class Main {

    public static void main(String[] args) {

        // ---------- SETUP ----------

        //  forbindelse til db (gennem Hibernate).
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

        // Henter TMDb API key (fra environment variable.)
        String apiKey = System.getenv("API_KEY");

        // Stopper, hvis API key mangler.
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "API_KEY environment variable mangler."
            );
        }

        // Opretter ApiReader, som kommunikerer med TMDb API.
        ApiReader apiReader = new ApiReader(apiKey);

// ==========================================================
// OPG1 – FETCH GENRES FRA TMDb og dto
// REST API + JSON → DTO med Jackson
// TMDb → JSON → GenreDTO → print
// Data gemmes ikke i databasen.
// ==========================================================

// TMDb endpoint til movie genres.
        String genreUrl = "https://api.themoviedb.org/3/genre/movie/list";

// Henter genres fra TMDb som JSON.
        String genreJson = apiReader.getGenres(genreUrl);

// JSON konverteres til DTO med Jackson.
        GenreResultDTO genreResultDTO =
                apiReader.convertGenresFromJson(genreJson);

// Viser alle genres fra TMDb.
        for (GenreDTO genreDTO : genreResultDTO.genres()) {
            System.out.println(
                    "Genre: " + genreDTO.id() + " - " + genreDTO.name()
            );
        }


// ==========================================================
// OPG2 – GODZILLA EKSEMPEL - DATA FRA TMDb
// Henter Godzilla fra TMDb.
// TMDb → JSON → CreditsResultDTO / MovieDetailsDTO → print
// Data gemmes ikke i databasen i denne del.
// ==========================================================

        String creditsUrl = "https://api.themoviedb.org/3/movie";

// Henter credits for Godzilla fra TMDb som JSON.
        String creditsJson =
                apiReader.getMovieCredits(creditsUrl, 1678);

// Konverterer credits JSON til CreditsResultDTO med Jackson.
        CreditsResultDTO creditsResultDTO =
                apiReader.convertCreditsFromJson(creditsJson);

// Henter details for Godzilla fra TMDb som JSON.
        String detailsJson =
                apiReader.getMovieDetails(creditsUrl, 1678);

// Konverterer details JSON til MovieDetailsDTO med Jackson.
        MovieDetailsDTO movieDetailsDTO =
                apiReader.convertMovieDetailsFromJson(detailsJson);

// Printer data fra TMDb.
        System.out.println(
                "\n========== OPG2 - LÆS GODZILLA FRA TMDb =========="
        );

        System.out.println("Title: " + movieDetailsDTO.title());

        System.out.println("\nGenres:");
        movieDetailsDTO.genres().forEach(
                genre -> System.out.println("- " + genre.name())
        );

        System.out.println("\nActors:");
        creditsResultDTO.cast()
                .stream()
                .limit(10)
                .forEach(actor ->
                        System.out.println("- " + actor.name())
                );

        System.out.println("\nDirector:");
        creditsResultDTO.crew()
                .stream()
                .filter(crew -> "Director".equals(crew.job()))
                .findFirst()
                .ifPresent(director ->
                        System.out.println("- " + director.name())
                );


// ==========================================================
// OPG3 – ANTAL DANSKE MOVIES FRA DE SIDSTE 5 ÅR
// Finder det samlede antal danske movies
// fra de sidste 5 år i TMDb.
// TMDb → JSON → MovieResultDTO → totalResults → print
// Data gemmes ikke i databasen.
// ==========================================================

// Endpoint, adressen til movies = /discover/movie.
        String checkUrl =
                "https://api.themoviedb.org/3/discover/movie";

// Henter første side som JSON.
// Side 1 indeholder også totalResults for alle sider.
        String checkJson =
                apiReader.discoverMovies(checkUrl, 1);

// JSON konverteres til DTO med Jackson.
        MovieResultDTO checkResult =
                apiReader.convertFromJson(checkJson);

// Printer det samlede antal movies.
        System.out.println(
                "\n========== OPG3 - DANSKE MOVIES FRA DE SIDSTE 5 ÅR =========="
        );

        System.out.println(
                "Danske movies fra de sidste 5 år er: "
                        + checkResult.totalResults()
        );

        // ---------- DAO LAYER ----------
        // Opretter DAO- og Service-objekter og bygger vejen til databasen.
// Service bruger DAO, og DAO bruger EntityManagerFactory til at kommunikere med databasen.

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
        // DANSKE MOVIES - ONCE-ONLY IMPORT
        //
        // Filmene er allerede importeret til databasen.
        // Sæt IMPORT_DANISH_MOVIES = true KUN hvis databasen
        // skal fyldes igen.
        // ==========================================================
        //once-only operation.
//         boolean IMPORT_DANISH_MOVIES = true;
        //Bestemmer om importen skal køre
        boolean IMPORT_DANISH_MOVIES = false;

        // ==========================================================
        // ==========================================================
// OPG4 + BONUS 3 + BONUS 4
// Importerer danske movies fra TMDb til databasen.
// BONUS 3: Synkroniserer databasen med TMDb.
// BONUS 4: Henter flere pages parallelt med Future.
// ==========================================================

        if (IMPORT_DANISH_MOVIES) {

            // Opretter en tom liste til AT GEMME alle movie-id'er fra TMDb.
            // Listen bruges senere til at sammenligne TMDb med databasen.
            List<Long> tmdbMovieIds = new ArrayList<>();
// URL til TMDb
            String moviesUrl =
                    "https://api.themoviedb.org/3/discover/movie";


            // ======================================================
            // HENTER FØRSTE SIDE
            // ======================================================

            // Henter FØRST side, som JSON.
            String firstJson =
                    apiReader.discoverMovies(moviesUrl, 1);

            // JSON -> DTO med Jackson.
            MovieResultDTO firstPage =
                    apiReader.convertFromJson(firstJson);

            // Viser hvor mange movies TMDb har fundet.
            System.out.println(
                    "Total movies: " + firstPage.totalResults()
            );
            // Viser hvor mange pages vi skal hente fra TMDb.
            System.out.println(
                    "Total pages: " + firstPage.totalPages()
            );


            // ======================================================
            // Til Bonus 4 bruger jeg ExecutorService, Future og 4 threads
            // BONUS 4 - PARALLEL FETCHING
            // ======================================================

            // Opretter en thread pool
            //med maksimum 4 threads.

            ExecutorService executor =
                    Executors.newFixedThreadPool(4);

            // Her gemmer vi Futures.
            //Future repræsenterer resultatet fra én TMDb-page.
            List<Future<MovieResultDTO>> futures =
                    new ArrayList<>();


         // Går igennem alle pages fra TMDb.
            for (int page = 1;
                 page <= firstPage.totalPages();
                 page++) {

                // Gemmer page-nummeret, så det kan bruges i lambdaen.
                int currentPage = page;

                // Sender opgaven til thread poolen.
                // En ledig thread henter denne page fra TMDb.
                Future<MovieResultDTO> future =
                        executor.submit(() -> {

                            System.out.println(
                                    "Henter side " + currentPage
                                            + " | Thread: "
                                            + Thread.currentThread().getName()
                            );
                            // Henter JSON fra den aktuelle TMDb-page.
                            String pageJson =
                                    apiReader.discoverMovies(
                                            moviesUrl,
                                            currentPage
                                    );

                            // JSON -> MovieResultDTO.
                            return apiReader.convertFromJson(
                                    pageJson
                            );
                        });

                // Gemmer Future i listen op.
                futures.add(future);
            }


            // ======================================================
            // HENTER RESULTATER FRA FUTURES
            // ======================================================

            try {

                // Går igennem alle Futures.
                for (Future<MovieResultDTO> future : futures) {

                    // get() henter resultatet.
                    // Hvis tasken ikke er færdig endnu,
                    // venter get() indtil resultatet er klar.
                    MovieResultDTO pageResult =
                            future.get();


                    // Går igennem alle movies på siden.
                    for (MovieDTO movieDTO :
                            pageResult.results()) {


                        // ==========================================
                        // BONUS 3
                        // ==========================================

                        // Gemmer movie-id'et fra TMDb.

                        tmdbMovieIds.add(
                                (long) movieDTO.id()
                        );


                        // ==========================================
                        // HENTER CREDITS
                        // ==========================================

                        String danishCreditsJson =
                                apiReader.getMovieCredits(
                                        creditsUrl,
                                        movieDTO.id()
                                );

                        // JSON -> CreditsResultDTO.
                        CreditsResultDTO danishCreditsResultDTO =
                                apiReader.convertCreditsFromJson(
                                        danishCreditsJson
                                );


                        // ==========================================
                        // HENTER MOVIE DETAILS
                        // ==========================================

                        String danishDetailsJson =
                                apiReader.getMovieDetails(
                                        creditsUrl,
                                        movieDTO.id()
                                );

                        // JSON -> MovieDetailsDTO.
                        MovieDetailsDTO danishDetailsDTO =
                                apiReader.convertMovieDetailsFromJson(
                                        danishDetailsJson
                                );


                        // ==========================================
                        // GEMMER MOVIE I DATABASE
                        // ==========================================

                        // Sender DTO'erne til Service Layer.
                        // Hvis movie allerede findes, oprettes den ikke igen.
                        // Hvis den er ny, gemmes den i databasen.
                        movieService.createMovieWithRelations(
                                movieDTO,
                                danishCreditsResultDTO,
                                danishDetailsDTO
                        );

                        System.out.println(
                                "Dansk movie: "
                                        + movieDTO.title()
                        );
                    }
                }

            } catch (Exception e) {

                throw new RuntimeException(e);

            } finally {

                // Lukker ExecutorService.
                executor.shutdown();
            }


            // ======================================================
            // BONUS 3 - SYNKRONISERING
            // ======================================================

            // Alle TMDb-pages er nu hentet.
            // Movies som findes i databasen,
            // men ikke længere findes i TMDb-listen,
            // slettes fra databasen.
            movieService.deleteMoviesNotInTmdb(
                    tmdbMovieIds
            );
        }


// ==========================================================
// OPG5 – LÆS MOVIE FRA DATABASEN
// ==========================================================

        System.out.println("\n========== OPG5 - MOVIE FRA DATABASE ==========");

        MovieResponseDTO movieFromDatabase =
                movieService.getAllMovies()
                        .stream()
                        .findFirst()
                        .orElseThrow();

        System.out.println(movieFromDatabase);


// ==========================================================
// OPG6 – ANTAL MOVIES I DATABASEN
// ==========================================================

        System.out.println("\n========== OPG6 - ANTAL MOVIES ==========");

        System.out.println(
                "Antal movies: " + movieService.getAllMovies().size()
        );


// ==========================================================
// OPG7 – LÆS ALLE MOVIES FRA DATABASEN
// ==========================================================
        System.out.println("\n========== OPG7 - ALL MOVIES ==========");

        List<MovieResponseDTO> allMovies = movieService.getAllMovies();

        System.out.println("Antal movies: " + allMovies.size());

        allMovies.stream()
                .limit(10)
                .forEach(movie ->
                        System.out.println(
                                movie.id() + " - "
                                        + movie.title()
                                        + " (" + movie.releaseDate() + ")"
                        )
                );

        System.out.println("... viser kun de første 10 movies");

// ==========================================================
// OPG8 – SØG MOVIE EFTER TITEL
// ==========================================================

        System.out.println("\n========== OPG8 - SEARCH: PROMISED ==========");

        movieService.searchByTitle("Promised")
                .forEach(System.out::println);


// ==========================================================
// OPG9 – GENNEMSNITLIG RATING
// ==========================================================

        System.out.println("\n========== OPG9 - AVERAGE RATING ==========");

        System.out.println(
                movieService.getAverageRating()
        );


// ==========================================================
// OPG10 – TOP 10 HIGHEST RATED
// ==========================================================

        System.out.println("\n========== OPG10 - TOP 10 HIGHEST RATED ==========");

        movieService.getTop10HighestRated()
                .forEach(System.out::println);


// ==========================================================
// OPG11 – TOP 10 LOWEST RATED
// ==========================================================

        System.out.println("\n========== OPG11 - TOP 10 LOWEST RATED ==========");

        movieService.getTop10LowestRated()
                .forEach(System.out::println);


// ==========================================================
// OPG12 – TOP 10 MOST POPULAR
// ==========================================================

        System.out.println("\n========== OPG12 - TOP 10 MOST POPULAR ==========");

        movieService.getTop10MostPopular()
                .forEach(System.out::println);


// ==========================================================
// OPG13 – LÆS ACTORS FRA DATABASEN
// ==========================================================

        System.out.println("\n========== OPG13 - ACTORS ==========");

        actorService.getAllActors()
                .stream()
                .limit(10)
                .forEach(System.out::println);


// ==========================================================
// OPG14 – LÆS DIRECTORS FRA DATABASEN
// ==========================================================

        System.out.println("\n========== OPG14 - DIRECTORS ==========");

        directorService.getAllDirectors()
                .stream()
                .limit(10)
                .forEach(System.out::println);


// ==========================================================
// OPG15 – LÆS GENRES FRA DATABASEN
// ==========================================================

        System.out.println("\n========== OPG15 - GENRES ==========");

        genreService.getAllGenres()
                .forEach(System.out::println);

        // ==========================================================
// OPG16 – FIND MOVIES BY GENRE
// ==========================================================

        System.out.println("\n========== OPG16 - MOVIES BY GENRE ==========");

// Finder movies som tilhører genre med database-id 1.
        movieService.getMoviesByGenre(1L)
                .stream()
                .limit(10)
                .forEach(System.out::println);
// ==========================================================
// BONUS 1 - Finder alle movies for en bestemt actor
// ==========================================================

        System.out.println(
                "\n========== BONUS 1 - Finder alle movies for en bestemt actor =========="
        );

        ActorResponseDTO actor =
                actorService.getActorById(1L);

        System.out.println("Actor: " + actor.name());

        actor.movies().forEach(movie ->
                System.out.println("Movie: " + movie)
        );
// ==========================================================
        // BONUS 2 - Finder alle movies for en bestemt director
        // ==========================================================
        System.out.println("\n========== Bonus 2- Finder alle movies for en bestemt director ==========");
        DirectorResponseDTO director =
                directorService.getDirectorById(1L);

        System.out.println("Director: " + director.name());

        director.movies().forEach(movie ->
                System.out.println("Movie: " + movie)
        );


// Lukker EntityManagerFactory når programmet er færdigt.
        emf.close();
    }


}


// EMF bruges af DAO'erne til at oprette EntityManagers
// og kommunikere med databasen.

// ApiReader er objektet, der kommunikerer med TMDb API'et.

// Main giver EMF til DAO'erne, så de kan arbejde med databasen.

// Service modtager de DAO'er, som den har brug for.

// Hvis IMPORT_DANISH_MOVIES = true,
// starter Main importen af danske movies.

// ApiReader henter movies fra TMDb som JSON.
//String firstJson = apiReader.discoverMovies(moviesUrl, 1);

// ApiReader konverterer JSON til DTO med Jackson.
//MovieResultDTO firstPage = apiReader.convertFromJson(firstJson);

// For hver movie hentes også credits og details fra TMDb.
// JSON-data konverteres til DTO'er.

// Main sender DTO'erne videre til MovieService.
//movieService.createMovieWithRelations(
//       movieDTO,
//     danishCreditsDTO,
//    danishDetailsDTO);

// Main opretter ikke Movie, Actor, Director og Genre Entities direkte.
// Main sender DTO'erne til MovieService.
// Service håndterer Entities og bruger DAO-laget til at gemme data i databasen.



//Main styrer hele flowet. Den kalder ApiReader for at hente data fra TMDb og sender DTO'erne videre til Service, som gennem DAO gemmer dem i databasen