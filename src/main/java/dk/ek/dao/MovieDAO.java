package dk.ek.dao;

import dk.ek.entity.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class MovieDAO extends GenericDAO<Movie> {

    public MovieDAO(EntityManagerFactory emf) {
        super(emf, Movie.class);
    }


    // Finder en movie ud fra TMDb ID.
    // Denne metode er specifik for Movie og findes derfor ikke i GenericDAO.
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

        em.close();

        return movie;
    }
    // SEARCH - Find movies by title
    public List<Movie> searchByTitle(String title) {

        EntityManager em = emf.createEntityManager();

        List<Movie> movies = em.createQuery(
                        "SELECT m FROM Movie m " +
                                "WHERE LOWER(m.title) LIKE LOWER(:title)",
                        Movie.class
                )
                .setParameter("title", "%" + title + "%")
                .getResultList();

        em.close();

        return movies;
    }
    // Gennemsnitlig rating
    public Double getAverageRating() {

        EntityManager em = emf.createEntityManager();

        Double average = em.createQuery(
                "SELECT AVG(m.rating) FROM Movie m WHERE m.rating > 0",
                Double.class
        ).getSingleResult();

        em.close();

        return average;
    }


    // Top 10 højeste rating
    public List<Movie> getTop10HighestRated() {

        EntityManager em = emf.createEntityManager();

        List<Movie> movies = em.createQuery(
                        "SELECT m FROM Movie m ORDER BY m.rating DESC",
                        Movie.class
                )
                .setMaxResults(10)
                .getResultList();

        em.close();

        return movies;
    }


    // Top 10 laveste rating
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

        em.close();

        return movies;
    }


    // Top 10 mest populære
    public List<Movie> getTop10MostPopular() {

        EntityManager em = emf.createEntityManager();

        List<Movie> movies = em.createQuery(
                        "SELECT m FROM Movie m ORDER BY m.popularity DESC",
                        Movie.class
                )
                .setMaxResults(10)
                .getResultList();

        em.close();

        return movies;
    }
}