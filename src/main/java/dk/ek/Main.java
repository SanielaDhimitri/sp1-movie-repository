package dk.ek;

import dk.ek.api.ApiReader;
import dk.ek.config.HibernateConfig;
import dk.ek.dao.ActorDAO;
import dk.ek.dao.DirectorDAO;
import dk.ek.dao.GenreDAO;
import dk.ek.dao.MovieDAO;
import dk.ek.dto.tmdb.*;
import dk.ek.entity.Actor;
import dk.ek.entity.Director;
import dk.ek.entity.Genre;
import dk.ek.entity.Movie;
import dk.ek.service.ActorService;
import dk.ek.service.DirectorService;
import dk.ek.service.GenreService;
import dk.ek.service.MovieService;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {

        // ---------- SETUP ----------

        // Opretter forbindelse til databasen gennem Hibernate.
        EntityManagerFactory emf =
                HibernateConfig.getEntityManagerFactory();

        // Henter TMDb API key fra environment variable.
        String apiKey = System.getenv("API_KEY");

        // Stopper programmet hvis API key mangler.
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "API_KEY environment variable mangler."
            );
        }

        // Opretter ApiReader, som kommunikerer med TMDb API.
        ApiReader apiReader = new ApiReader(apiKey);


        // ---------- GENRES FRA TMDb ----------

        // TMDb endpoint til movie genres.
        String genreUrl =
                "https://api.themoviedb.org/3/genre/movie/list";

        // Henter genres fra TMDb som JSON.
        String genreJson =
                apiReader.getGenres(genreUrl);

        // Konverterer JSON til GenreResultDTO med Jackson.
        GenreResultDTO genreResultDTO =
                apiReader.convertGenresFromJson(genreJson);

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

        // Opretter services, som bruger DAO-laget.
        MovieService movieService = new MovieService(movieDAO);
        ActorService actorService = new ActorService(actorDAO);
        DirectorService directorService = new DirectorService(directorDAO);
        GenreService genreService = new GenreService(genreDAO);


        // ==========================================================
        // GODZILLA EKSEMPEL
        // Viser processen for én movie:
        // TMDb API -> JSON -> DTO -> Entity -> DAO -> Database
        // ==========================================================

        // TMDb adresse/endpoint til movie credits og details.
        String creditsUrl =
                "https://api.themoviedb.org/3/movie";

        // Henter credits (actors og crew) for Godzilla, TMDb ID 1678.
        String creditsJson =
                apiReader.getMovieCredits(creditsUrl, 1678);

        // Konverterer credits JSON til CreditsDTO med Jackson.
        CreditsDTO creditsDTO =
                apiReader.convertCreditsFromJson(creditsJson);

        // Opretter en Movie entity for Godzilla.
        Movie movie = new Movie(
                1678L,
                "Godzilla",
                LocalDate.of(1954, 11, 3),
                7.6,
                10.0
        );

        // Henter detaljer for Godzilla fra TMDb som JSON.
        String detailsJson =
                apiReader.getMovieDetails(creditsUrl, 1678);

        // Konverterer JSON til MovieDetailsDTO med Jackson.
        MovieDetailsDTO movieDetailsDTO =
                apiReader.convertMovieDetailsFromJson(detailsJson);


        // ---------- GODZILLA GENRES ----------

        // Gennemgår alle genres, som Godzilla har.
        for (GenreDTO genreDTO : movieDetailsDTO.genres()) {

            // Tjekker om genren allerede findes i databasen.
            Genre genre =
                    genreDAO.findByTmdbId(genreDTO.id());

            // Hvis genren ikke findes, oprettes og gemmes den.
            if (genre == null) {
                genre = new Genre(
                        genreDTO.id(),
                        genreDTO.name()
                );

                genreDAO.create(genre);
            }

            // Tilføjer genren til Godzilla.
            movie.getGenres().add(genre);

            System.out.println(
                    "Godzilla Genre: " + genre.getName()
            );
        }

        // Viser antal actors og crew-medlemmer fra TMDb.
        System.out.println(
                "Actors: " + creditsDTO.cast().size()
        );

        System.out.println(
                "Crew: " + creditsDTO.crew().size()
        );


        // ---------- GODZILLA ACTORS ----------

        // Gennemgår og gemmer actors i databasen.
        for (ActorDTO actorDTO : creditsDTO.cast()) {

            // Tjekker om actor allerede findes.
            Actor actor =
                    actorDAO.findByTmdbId(actorDTO.id());

            // Hvis actor ikke findes, oprettes og gemmes actor.
            if (actor == null) {
                actor = new Actor(
                        actorDTO.id(),
                        actorDTO.name()
                );

                actorDAO.create(actor);
            }

            // Tilføjer actor til Godzilla.
            movie.getActors().add(actor);

            System.out.println(
                    "Actor: " + actor.getName()
            );
        }


        // ---------- GODZILLA DIRECTOR ----------

        // Gennemgår crew og finder personen med jobbet Director.
        for (CrewDTO crewDTO : creditsDTO.crew()) {

            if ("Director".equals(crewDTO.job())) {

                // Tjekker om director allerede findes i databasen.
                Director director =
                        directorDAO.findByTmdbId(crewDTO.id());

                // Hvis director ikke findes, oprettes og gemmes director.
                if (director == null) {
                    director = new Director(
                            crewDTO.id(),
                            crewDTO.name()
                    );

                    directorDAO.create(director);
                }

                // Tilføjer director til Godzilla.
                movie.setDirector(director);

                System.out.println(
                        "Director: " + director.getName()
                );

                // Vi har fundet director og stopper derfor loopet.
                break;
            }
        }


        // ---------- GEMMER GODZILLA ----------

        // Tjekker om Godzilla allerede findes i databasen.
        Movie existingGodzilla =
                movieDAO.findByTmdbId(1678L);

        // Gemmer kun Godzilla hvis den ikke allerede findes.
        if (existingGodzilla == null) {
            movieDAO.create(movie);
            System.out.println("Godzilla gemt.");
        } else {
            System.out.println("Godzilla findes allerede.");
        }


        // ==========================================================
        // DANSKE MOVIES
        // Henter alle danske movies fra de seneste år fra TMDb.
        // Samme princip som Godzilla-eksemplet bruges for hver movie.
        // ==========================================================

        // TMDb endpoint til discover movies.
        String moviesUrl =
                "https://api.themoviedb.org/3/discover/movie";

        // Henter første side for at finde total antal movies og pages.
        String firstJson =
                apiReader.discoverMovies(moviesUrl, 1);

        // Konverterer JSON fra første side til MovieResultDTO.
        MovieResultDTO firstPage =
                apiReader.convertFromJson(firstJson);

        // Viser hvor mange movies og pages TMDb returnerer.
        System.out.println(
                "Total movies: " + firstPage.totalResults()
        );

        System.out.println(
                "Total pages: " + firstPage.totalPages()
        );


        // ---------- GENNEMGÅR ALLE PAGES ----------

        // Går igennem alle sider fra TMDb.
        for (int page = 1; page <= firstPage.totalPages(); page++) {

            System.out.println(
                    "\nHenter side "
                            + page
                            + " af "
                            + firstPage.totalPages()
            );

            // Henter den aktuelle side som JSON.
            String pageJson =
                    apiReader.discoverMovies(moviesUrl, page);

            // Konverterer JSON til MovieResultDTO.
            MovieResultDTO pageResult =
                    apiReader.convertFromJson(pageJson);


            // ---------- GENNEMGÅR MOVIES PÅ SIDEN ----------

            for (MovieDTO movieDTO : pageResult.results()) {

                // Tjekker om movie allerede findes i databasen.
                Movie existingMovie =
                        movieDAO.findByTmdbId((long) movieDTO.id());

                // Hvis movie allerede findes, går vi videre til næste movie.
                if (existingMovie != null) {
                    System.out.println(
                            "Movie findes allerede: "
                                    + existingMovie.getTitle()
                    );
                    continue;
                }


                // ---------- RELEASE DATE ----------

                // Release date kan være tom, derfor starter den som null.
                LocalDate releaseDate = null;

                // Konverterer release date fra String til LocalDate.
                if (movieDTO.releaseDate() != null
                        && !movieDTO.releaseDate().isBlank()) {

                    releaseDate =
                            LocalDate.parse(movieDTO.releaseDate());
                }


                // ---------- MOVIE ENTITY ----------

                // Konverterer MovieDTO-data til en Movie entity.
                Movie danishMovie = new Movie(
                        (long) movieDTO.id(),
                        movieDTO.title(),
                        releaseDate,
                        movieDTO.rating(),
                        movieDTO.popularity()
                );


                // ---------- CREDITS ----------

                // Henter credits for denne movie fra TMDb.
                String danishCreditsJson =
                        apiReader.getMovieCredits(
                                creditsUrl,
                                movieDTO.id()
                        );

                // Konverterer credits JSON til CreditsDTO.
                CreditsDTO danishCreditsDTO =
                        apiReader.convertCreditsFromJson(
                                danishCreditsJson
                        );


                // ---------- ACTORS ----------

                // Gennemgår alle actors for denne movie.
                for (ActorDTO actorDTO : danishCreditsDTO.cast()) {

                    // Tjekker om actor allerede findes i databasen.
                    Actor actor =
                            actorDAO.findByTmdbId(actorDTO.id());

                    // Opretter actor hvis actor ikke allerede findes.
                    if (actor == null) {
                        actor = new Actor(
                                actorDTO.id(),
                                actorDTO.name()
                        );

                        actorDAO.create(actor);
                    }

                    // Tilføjer actor til movie.
                    danishMovie.getActors().add(actor);
                }


                // ---------- DIRECTOR ----------

                // Gennemgår crew og finder Director.
                for (CrewDTO crewDTO : danishCreditsDTO.crew()) {

                    if ("Director".equals(crewDTO.job())) {

                        // Tjekker om director allerede findes.
                        Director director =
                                directorDAO.findByTmdbId(
                                        crewDTO.id()
                                );

                        // Opretter director hvis director ikke findes.
                        if (director == null) {

                            director = new Director(
                                    crewDTO.id(),
                                    crewDTO.name()
                            );

                            directorDAO.create(director);
                        }

                        // Tilføjer director til movie.
                        danishMovie.setDirector(director);

                        // Stopper når director er fundet.
                        break;
                    }
                }


                // ---------- GENRES ----------

                // Henter movie details for at få genres.
                String danishDetailsJson =
                        apiReader.getMovieDetails(
                                creditsUrl,
                                movieDTO.id()
                        );

                // Konverterer details JSON til MovieDetailsDTO.
                MovieDetailsDTO danishDetailsDTO =
                        apiReader.convertMovieDetailsFromJson(
                                danishDetailsJson
                        );

                // Gennemgår alle genres for denne movie.
                for (GenreDTO genreDTO : danishDetailsDTO.genres()) {

                    // Tjekker om genre allerede findes i databasen.
                    Genre genre =
                            genreDAO.findByTmdbId(genreDTO.id());

                    // Opretter genre hvis den ikke findes.
                    if (genre == null) {
                        genre = new Genre(
                                genreDTO.id(),
                                genreDTO.name()
                        );

                        genreDAO.create(genre);
                    }

                    // Tilføjer genre til movie.
                    danishMovie.getGenres().add(genre);
                }


                // ---------- GEMMER MOVIE ----------

                // Gemmer den færdige movie med relationships i databasen.
                movieDAO.create(danishMovie);

                System.out.println(
                        "Dansk movie: " + danishMovie.getTitle()
                );
            }
        }


        // ==========================================================
        // SERVICE LAYER
        // Henter data fra vores egen database gennem services.
        // ==========================================================

        // ---------- ALLE MOVIES ----------

        System.out.println(
                "\n--- MOVIES FRA DATABASE ---"
        );

        // Henter og viser alle movies fra databasen.
        movieService.getAllMovies().forEach(movieResponse -> {

            System.out.println(
                    movieResponse.title()
                            + " | TMDb ID: "
                            + movieResponse.tmdbId()
                            + " | Rating: "
                            + movieResponse.rating()
            );
        });


        // ---------- ACTORS OG MOVIES ----------

        System.out.println(
                "\n--- ACTORS OG DERES MOVIES ---"
        );

        // Henter alle actors og viser deres movies.
        actorService.getAllActors().forEach(actor -> {

            System.out.println(
                    "\nActor: " + actor.name()
            );

            actor.movies().forEach(movieTitle ->
                    System.out.println(
                            " - " + movieTitle
                    )
            );
        });


        // ---------- DIRECTORS OG MOVIES ----------

        System.out.println(
                "\n--- DIRECTORS OG DERES MOVIES ---"
        );

        // Henter alle directors og viser deres movies.
        directorService.getAllDirectors().forEach(director -> {

            System.out.println(
                    "\nDirector: " + director.name()
            );

            director.movies().forEach(movieTitle ->
                    System.out.println(
                            " - " + movieTitle
                    )
            );
        });


        // ---------- GENRES OG MOVIES ----------

        System.out.println(
                "\n--- GENRES OG DERES MOVIES ---"
        );

        // Henter alle genres og viser movies inden for hver genre.
        genreService.getAllGenres().forEach(genre -> {

            System.out.println(
                    "\nGenre: " + genre.name()
            );

            genre.movies().forEach(movieTitle ->
                    System.out.println(
                            " - " + movieTitle
                    )
            );
        });


        // ==========================================================
        // SEARCH
        // ==========================================================

        System.out.println(
                "\n--- SEARCH MOVIE BY TITLE ---"
        );

        // Søger efter movies hvor titlen indeholder "land".
        movieService.searchByTitle("land")
                .forEach(movieDTO -> {

                    System.out.println(
                            movieDTO.title()
                                    + " | Rating: "
                                    + movieDTO.rating()
                    );
                });


        // ==========================================================
        // MOVIE STATISTICS
        // ==========================================================

        System.out.println(
                "\n--- MOVIE STATISTICS ---"
        );


        // ---------- AVERAGE RATING ----------

        // Beregner den gennemsnitlige rating for alle movies.
        Double averageRating =
                movieService.getAverageRating();

        System.out.println(
                "Average rating: " + averageRating
        );


        // ---------- TOP 10 HIGHEST RATED ----------

        System.out.println(
                "\n--- TOP 10 HIGHEST RATED ---"
        );

        // Henter de 10 movies med højeste rating.
        movieService.getTop10HighestRated()
                .forEach(movieDTO ->
                        System.out.println(
                                movieDTO.title()
                                        + " | Rating: "
                                        + movieDTO.rating()
                        )
                );


        // ---------- TOP 10 LOWEST RATED ----------

        System.out.println(
                "\n--- TOP 10 LOWEST RATED ---"
        );

        // Henter de 10 movies med laveste rating.
        movieService.getTop10LowestRated()
                .forEach(movieDTO ->
                        System.out.println(
                                movieDTO.title()
                                        + " | Rating: "
                                        + movieDTO.rating()
                        )
                );


        // ---------- TOP 10 MOST POPULAR ----------

        System.out.println(
                "\n--- TOP 10 MOST POPULAR ---"
        );

        // Henter de 10 mest populære movies.
        movieService.getTop10MostPopular()
                .forEach(movieDTO ->
                        System.out.println(
                                movieDTO.title()
                                        + " | Popularity: "
                                        + movieDTO.popularity()
                        )
                );


        // ---------- SHUTDOWN ----------

        // Lukker forbindelsen til databasen når programmet er færdigt.
        emf.close();
    }
}