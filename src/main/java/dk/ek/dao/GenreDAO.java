package dk.ek.dao;

import dk.ek.entity.Genre;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class GenreDAO extends GenericDAO<Genre> {

    public GenreDAO(EntityManagerFactory emf) {
        super(emf, Genre.class);
    }


    // READ - Find all genres with their movies
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


    // READ - Find by TMDb ID
    public Genre findByTmdbId(Long tmdbId) {

        EntityManager em = emf.createEntityManager();

        Genre genre = em.createQuery(
                        "SELECT g FROM Genre g WHERE g.tmdbId = :tmdbId",
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