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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class Main {

    public static void main(String[] args) {


        // ==========================================================
        // SETUP
        // ==========================================================

        // Forbindelse til databasen gennem Hibernate.
        EntityManagerFactory emf =
                HibernateConfig.getEntityManagerFactory();

        // Henter TMDb API key fra environment variable.
        String apiKey = System.getenv("API_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "API_KEY environment variable mangler."
            );
        }

        // Kommunikerer med TMDb API.
        ApiReader apiReader = new ApiReader(apiKey);


        // ==========================================================
        // DAO LAYER
        // ==========================================================

        MovieDAO movieDAO = new MovieDAO(emf);
        DirectorDAO directorDAO = new DirectorDAO(emf);
        ActorDAO actorDAO = new ActorDAO(emf);
        GenreDAO genreDAO = new GenreDAO(emf);


        // ==========================================================
        // SERVICE LAYER
        // ==========================================================

        MovieService movieService =
                new MovieService(
                        movieDAO,
                        actorDAO,
                        directorDAO,
                        genreDAO
                );

        ActorService actorService =
                new ActorService(actorDAO);

        DirectorService directorService =
                new DirectorService(directorDAO);

        GenreService genreService =
                new GenreService(genreDAO);


        // TMDb endpoint til movie credits/details.
        String creditsUrl =
                "https://api.themoviedb.org/3/movie";


        // ==========================================================
        // OPG1
        // DANSKE MOVIES FRA DE SIDSTE 5 ÅR → DATABASE
        // ==========================================================

        // Sæt true når importen skal køres.
        // Efter data er gemt kan den sættes til false.
        boolean IMPORT_DANISH_MOVIES = false;


        if (IMPORT_DANISH_MOVIES) {

            // Gemmer alle movie-id'er fra TMDb.
            // Bruges senere til BONUS 3 - synkronisering.
            List<Long> tmdbMovieIds =
                    new ArrayList<>();


            String moviesUrl =
                    "https://api.themoviedb.org/3/discover/movie";


            // ======================================================
            // HENTER FØRSTE PAGE
            // ======================================================

            String firstJson =
                    apiReader.discoverMovies(
                            moviesUrl,
                            1
                    );


            // JSON → DTO med Jackson.
            MovieResultDTO firstPage =
                    apiReader.convertFromJson(
                            firstJson
                    );


            System.out.println(
                    "Total movies: "
                            + firstPage.totalResults()
            );


            System.out.println(
                    "Total pages: "
                            + firstPage.totalPages()
            );


            // ======================================================
            // BONUS 4
            // PARALLEL FETCHING MED EXECUTORSERVICE + FUTURE
            // ======================================================

            // Maksimum 4 threads arbejder parallelt.
            ExecutorService executor =
                    Executors.newFixedThreadPool(4);


            // Her gemmes resultaterne,
            // som kommer tilbage senere.
            List<Future<MovieResultDTO>> futures =
                    new ArrayList<>();


            // Går igennem alle TMDb pages.
            for (int page = 1;
                 page <= firstPage.totalPages();
                 page++) {


                int currentPage = page;


                // Sender arbejdet til ExecutorService.
                Future<MovieResultDTO> future =
                        executor.submit(() -> {


                            System.out.println(
                                    "Henter side "
                                            + currentPage
                                            + " | Thread: "
                                            + Thread.currentThread()
                                            .getName()
                            );


                            // Henter page som JSON.
                            String pageJson =
                                    apiReader.discoverMovies(
                                            moviesUrl,
                                            currentPage
                                    );


                            // JSON → DTO.
                            return apiReader.convertFromJson(
                                    pageJson
                            );
                        });


                // Gemmer Future.
                futures.add(future);
            }


            // ======================================================
            // HENTER RESULTATER FRA FUTURES
            // ======================================================

            try {

                for (Future<MovieResultDTO> future : futures) {


                    // Henter resultatet fra Future.
                    MovieResultDTO pageResult =
                            future.get();


                    // Går igennem alle movies på denne page.
                    for (MovieDTO movieDTO :
                            pageResult.results()) {


                        // ==========================================
                        // BONUS 3
                        // GEMMER TMDb MOVIE ID
                        // ==========================================

                        tmdbMovieIds.add(
                                (long) movieDTO.id()
                        );


                        // ==========================================
                        // HENTER CREDITS
                        // Actors + Director
                        // ==========================================

                        String danishCreditsJson =
                                apiReader.getMovieCredits(
                                        creditsUrl,
                                        movieDTO.id()
                                );


                        // JSON → CreditsResultDTO.
                        CreditsResultDTO danishCreditsResultDTO =
                                apiReader.convertCreditsFromJson(
                                        danishCreditsJson
                                );


                        // ==========================================
                        // HENTER MOVIE DETAILS
                        // Genres + movie details
                        // ==========================================

                        String danishDetailsJson =
                                apiReader.getMovieDetails(
                                        creditsUrl,
                                        movieDTO.id()
                                );


                        // JSON → MovieDetailsDTO.
                        MovieDetailsDTO danishDetailsDTO =
                                apiReader.convertMovieDetailsFromJson(
                                        danishDetailsJson
                                );


                        // ==========================================
                        // GEMMER MOVIE + RELATIONER I DATABASE
                        // ==========================================

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
            // BONUS 3
            // SYNKRONISERER DATABASE MED TMDb
            // ======================================================

            movieService.deleteMoviesNotInTmdb(
                    tmdbMovieIds
            );
        }


        // ==========================================================
        // OPG2
        // LISTE OVER ALLE MOVIES FRA DATABASE
        // ==========================================================

        System.out.println(
                "\n========== OPG2 - ALLE MOVIES =========="
        );


        List<MovieResponseDTO> allMovies =
                movieService.getAllMovies();


        System.out.println(
                "Antal movies: "
                        + allMovies.size()
        );


        allMovies.forEach(movie ->
                System.out.println(
                        movie.id()
                                + " - "
                                + movie.title()
                                + " ("
                                + movie.releaseDate()
                                + ")"
                )
        );


        // ==========================================================
        // OPG3
        // LISTE OVER ALLE ACTORS OG DIRECTORS
        // ==========================================================


        // ---------- ACTORS ----------

        System.out.println(
                "\n========== OPG3 - ALLE ACTORS =========="
        );


        actorService.getAllActors()
                .forEach(System.out::println);


        // ---------- DIRECTORS ----------

        System.out.println(
                "\n========== OPG3 - ALLE DIRECTORS =========="
        );


        directorService.getAllDirectors()
                .forEach(System.out::println);



        // ==========================================================
        // OPG4
        // ALLE GENRES + MOVIES INDEN FOR EN BESTEMT GENRE
        // ==========================================================


        // ---------- ALLE GENRES ----------

        System.out.println(
                "\n========== OPG4 - ALLE GENRES =========="
        );


        genreService.getAllGenres()
                .forEach(System.out::println);


        // ---------- MOVIES BY GENRE ----------

        System.out.println(
                "\n========== OPG4 - MOVIES BY GENRE =========="
        );


        // Eksempel:
        // Finder movies som tilhører genre med database-id 1.
        movieService.getMoviesByGenre(1L)
                .forEach(System.out::println);



        // ==========================================================
        // OPG5
        // CRUD - CREATE / UPDATE / DELETE MOVIE
        // ==========================================================

        /*
         * Her skal bruges de CREATE, UPDATE og DELETE metoder,
         * som allerede findes i MovieService.
         *
         * CREATE:
         * Opret en ny movie i databasen.
         *
         * UPDATE:
         * Opdater mindst title og releaseDate.
         *
         * DELETE:
         * Slet en movie fra databasen.
         *
         * Vi indsætter de præcise kald her,
         * når vi bruger signaturerne fra din MovieService.
         */


        // ==========================================================
        // OPG6
        // SEARCH MOVIE BY TITLE
        // Case-insensitive
        // ==========================================================

        System.out.println(
                "\n========== OPG6 - SEARCH: LOVE =========="
        );


        movieService.searchByTitle("love")
                .forEach(System.out::println);



        // ==========================================================
        // OPG7
        // MOVIE STATISTICS
        // ==========================================================


        // ---------- GENNEMSNITLIG RATING ----------

        System.out.println(
                "\n========== OPG7 - AVERAGE RATING =========="
        );


        System.out.println(
                movieService.getAverageRating()
        );


        // ---------- TOP 10 HIGHEST RATED ----------

        System.out.println(
                "\n========== OPG7 - TOP 10 HIGHEST RATED =========="
        );


        movieService.getTop10HighestRated()
                .forEach(System.out::println);


        // ---------- TOP 10 LOWEST RATED ----------

        System.out.println(
                "\n========== OPG7 - TOP 10 LOWEST RATED =========="
        );


        movieService.getTop10LowestRated()
                .forEach(System.out::println);


        // ---------- TOP 10 MOST POPULAR ----------

        System.out.println(
                "\n========== OPG7 - TOP 10 MOST POPULAR =========="
        );


        movieService.getTop10MostPopular()
                .forEach(System.out::println);



        // ==========================================================
        // BONUS 1
        // ALLE MOVIES FOR EN BESTEMT ACTOR
        // ==========================================================

        System.out.println(
                "\n========== BONUS 1 - MOVIES BY ACTOR =========="
        );


        ActorResponseDTO actor =
                actorService.getActorById(1L);


        System.out.println(
                "Actor: " + actor.name()
        );


        actor.movies().forEach(movie ->
                System.out.println(
                        "Movie: " + movie
                )
        );



        // ==========================================================
        // BONUS 2
        // ALLE MOVIES FOR EN BESTEMT DIRECTOR
        // ==========================================================

        System.out.println(
                "\n========== BONUS 2 - MOVIES BY DIRECTOR =========="
        );


        DirectorResponseDTO director =
                directorService.getDirectorById(1L);


        System.out.println(
                "Director: " + director.name()
        );


        director.movies().forEach(movie ->
                System.out.println(
                        "Movie: " + movie
                )
        );



        // ==========================================================
        // BONUS 3
        // SYNKRONISERING
        // ==========================================================
        //
        // BONUS 3 ligger inde i OPG1-importen:
        //
        // tmdbMovieIds.add(...)
        //
        // og til sidst:
        //
        // movieService.deleteMoviesNotInTmdb(tmdbMovieIds);
        //



        // ==========================================================
        // BONUS 4
        // EXECUTORSERVICE + FUTURE
        // ==========================================================
        //
        // BONUS 4 ligger også inde i OPG1-importen.
        //
        // ExecutorService:
        //
        // Executors.newFixedThreadPool(4)
        //
        // Future:
        //
        // Future<MovieResultDTO>
        //
        // På den måde hentes flere TMDb pages parallelt.
        //



        // ==========================================================
        // LUK DATABASEFORBINDELSEN
        // ==========================================================

        emf.close();
    }
}