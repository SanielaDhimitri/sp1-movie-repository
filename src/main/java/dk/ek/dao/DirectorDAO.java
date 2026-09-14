package dk.ek.dao;

import dk.ek.entity.Director;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class DirectorDAO extends GenericDAO<Director> {

    public DirectorDAO(EntityManagerFactory emf) {
        super(emf, Director.class);
    }


    // READ - Find all directors with their movies
    @Override
    public List<Director> findAll() {

        EntityManager em = emf.createEntityManager();

        List<Director> directors = em.createQuery(
                "SELECT DISTINCT d FROM Director d LEFT JOIN FETCH d.movies",
                Director.class
        ).getResultList();

        em.close();

        return directors;
    }


    // READ - Find by TMDb ID
    public Director findByTmdbId(Long tmdbId) {

        EntityManager em = emf.createEntityManager();

        Director director = em.createQuery(
                        "SELECT d FROM Director d WHERE d.tmdbId = :tmdbId",
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