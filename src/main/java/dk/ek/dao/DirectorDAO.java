package dk.ek.dao;

import dk.ek.entity.Director;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class DirectorDAO extends GenericDAO<Director> {

    public DirectorDAO(EntityManagerFactory emf) {
        super(emf, Director.class);
    }


    // =========================================================
    // READ ALL - Find all directors with their movies
    // =========================================================

    @Override
    public List<Director> findAll() {

        EntityManager em = emf.createEntityManager();

        List<Director> directors = em.createQuery(
                "SELECT DISTINCT d FROM Director d " +
                        "LEFT JOIN FETCH d.movies",
                Director.class
        ).getResultList();

        em.close();

        return directors;
    }


    // =========================================================
    // READ BY DATABASE ID - with movies
    // =========================================================

    @Override
    public Director findById(Long id) {

        EntityManager em = emf.createEntityManager();

        Director director = em.createQuery(
                        "SELECT DISTINCT d FROM Director d " +
                                "LEFT JOIN FETCH d.movies " +
                                "WHERE d.id = :id",
                        Director.class
                )
                .setParameter("id", id)
                .getResultStream()
                .findFirst()
                .orElse(null);

        em.close();

        return director;
    }


    // =========================================================
    // READ BY TMDB ID - with movies
    // =========================================================

    public Director findByTmdbId(Long tmdbId) {

        EntityManager em = emf.createEntityManager();

        Director director = em.createQuery(
                        "SELECT DISTINCT d FROM Director d " +
                                "LEFT JOIN FETCH d.movies " +
                                "WHERE d.tmdbId = :tmdbId",
                        Director.class
                )
                .setParameter("tmdbId", tmdbId)
                .getResultStream()
                .findFirst()
                .orElse(null);

        em.close();

        return director;
    }
}