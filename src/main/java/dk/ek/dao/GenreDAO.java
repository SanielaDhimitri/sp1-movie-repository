package dk.ek.dao;

import dk.ek.entity.Genre;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class GenreDAO extends GenericDAO<Genre> {

    public GenreDAO(EntityManagerFactory emf) {
        super(emf, Genre.class);
    }


    // READ - Find all genres with movies
    @Override
    public List<Genre> findAll() {

        EntityManager em = emf.createEntityManager();

        List<Genre> genres = em.createQuery(
                "SELECT DISTINCT g FROM Genre g LEFT JOIN FETCH g.movies",
                Genre.class
        ).getResultList();

        em.close();

        return genres;
    }


    // READ - Find genre by database ID with movies
    @Override
    public Genre findById(Long id) {

        EntityManager em = emf.createEntityManager();

        Genre genre = em.createQuery(
                        "SELECT DISTINCT g FROM Genre g " +
                                "LEFT JOIN FETCH g.movies " +
                                "WHERE g.id = :id",
                        Genre.class
                )
                .setParameter("id", id)
                .getResultStream()
                .findFirst()
                .orElse(null);

        em.close();

        return genre;
    }


    // READ - Find genre by TMDb ID with movies
    public Genre findByTmdbId(Long tmdbId) {

        EntityManager em = emf.createEntityManager();

        Genre genre = em.createQuery(
                        "SELECT DISTINCT g FROM Genre g " +
                                "LEFT JOIN FETCH g.movies " +
                                "WHERE g.tmdbId = :tmdbId",
                        Genre.class
                )
                .setParameter("tmdbId", tmdbId)
                .getResultStream()
                .findFirst()
                .orElse(null);

        em.close();

        return genre;
    }
}