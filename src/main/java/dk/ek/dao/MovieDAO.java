package dk.ek.dao;

import dk.ek.entity.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class MovieDAO extends GenericDAO<Movie> {

    public MovieDAO(EntityManagerFactory emf) {
        super(emf, Movie.class);
    }


    // =========================================================
    // READ - FIND ALL WITH RELATIONSHIPS
    // =========================================================

    @Override
    public List<Movie> findAll() {

        EntityManager em = emf.createEntityManager();

        List<Movie> movies = em.createQuery(
                "SELECT m FROM Movie m",
                Movie.class
        ).getResultList();

        initializeRelationships(movies);

        em.close();

        return movies;
    }


    // =========================================================
    // READ - FIND BY DATABASE ID WITH RELATIONSHIPS
    // =========================================================

    @Override
    public Movie findById(Long id) {

        EntityManager em = emf.createEntityManager();

        Movie movie = em.find(Movie.class, id);

        initializeRelationships(movie);

        em.close();

        return movie;
    }


    // =========================================================
    // READ - FIND BY TMDB ID WITH RELATIONSHIPS
    // =========================================================

    public Movie findByTmdbId(Long tmdbId) {

        EntityManager em = emf.createEntityManager();

        Movie movie = em.createQuery(
                        "SELECT m FROM Movie m WHERE m.tmdbId = :tmdbId",
                        Movie.class
                )
                .setParameter("tmdbId", tmdbId)
                .getResultStream()
                .findFirst()
                .orElse(null);

        initializeRelationships(movie);

        em.close();

        return movie;
    }


    // =========================================================
    // SEARCH BY TITLE
    // =========================================================

    public List<Movie> searchByTitle(String title) {

        EntityManager em = emf.createEntityManager();

        List<Movie> movies = em.createQuery(
                        "SELECT m FROM Movie m " +
                                "WHERE LOWER(m.title) LIKE LOWER(:title)",
                        Movie.class
                )
                .setParameter("title", "%" + title + "%")
                .getResultList();

        initializeRelationships(movies);

        em.close();

        return movies;
    }


    // =========================================================
    // AVERAGE RATING
    // =========================================================

    public Double getAverageRating() {

        EntityManager em = emf.createEntityManager();

        Double average = em.createQuery(
                "SELECT AVG(m.rating) FROM Movie m WHERE m.rating > 0",
                Double.class
        ).getSingleResult();

        em.close();

        return average;
    }


    // =========================================================
    // TOP 10 HIGHEST RATED
    // =========================================================

    public List<Movie> getTop10HighestRated() {

        EntityManager em = emf.createEntityManager();

        List<Movie> movies = em.createQuery(
                        "SELECT m FROM Movie m ORDER BY m.rating DESC",
                        Movie.class
                )
                .setMaxResults(10)
                .getResultList();

        initializeRelationships(movies);

        em.close();

        return movies;
    }


    // =========================================================
    // TOP 10 LOWEST RATED
    // =========================================================

    public List<Movie> getTop10LowestRated() {

        EntityManager em = emf.createEntityManager();

        List<Movie> movies = em.createQuery(
                        "SELECT m FROM Movie m " +
                                "WHERE m.rating > 0 " +
                                "ORDER BY m.rating ASC",
                        Movie.class
                )
                .setMaxResults(10)
                .getResultList();

        initializeRelationships(movies);

        em.close();

        return movies;
    }


    // =========================================================
    // TOP 10 MOST POPULAR
    // =========================================================

    public List<Movie> getTop10MostPopular() {

        EntityManager em = emf.createEntityManager();

        List<Movie> movies = em.createQuery(
                        "SELECT m FROM Movie m ORDER BY m.popularity DESC",
                        Movie.class
                )
                .setMaxResults(10)
                .getResultList();

        initializeRelationships(movies);

        em.close();

        return movies;
    }


    // =========================================================
    // FIND MOVIES BY GENRE
    // =========================================================

    public List<Movie> findMoviesByGenre(Long genreId) {

        EntityManager em = emf.createEntityManager();

        List<Movie> movies = em.createQuery(
                        "SELECT DISTINCT m FROM Movie m " +
                                "JOIN m.genres g " +
                                "WHERE g.id = :genreId",
                        Movie.class
                )
                .setParameter("genreId", genreId)
                .getResultList();

        initializeRelationships(movies);

        em.close();

        return movies;
    }


    // =========================================================
    // INITIALIZE RELATIONSHIPS
    // =========================================================

    private void initializeRelationships(Movie movie) {

        if (movie != null) {
            movie.getActors().size();
            movie.getGenres().size();

            if (movie.getDirector() != null) {
                movie.getDirector().getName();
            }
        }
    }


    private void initializeRelationships(List<Movie> movies) {

        movies.forEach(this::initializeRelationships);
    }
}