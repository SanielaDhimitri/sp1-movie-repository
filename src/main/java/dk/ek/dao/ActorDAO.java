package dk.ek.dao;

import dk.ek.entity.Actor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class ActorDAO extends GenericDAO<Actor> {

    public ActorDAO(EntityManagerFactory emf) {
        super(emf, Actor.class);
    }


    // READ - Find all actors with their movies
    @Override
    public List<Actor> findAll() {

        EntityManager em = emf.createEntityManager();

        List<Actor> actors = em.createQuery(
                "SELECT DISTINCT a FROM Actor a LEFT JOIN FETCH a.movies",
                Actor.class
        ).getResultList();

        em.close();

        return actors;
    }


    // READ - Find by TMDb ID
    public Actor findByTmdbId(Long tmdbId) {

        EntityManager em = emf.createEntityManager();

        Actor actor = em.createQuery(
                        "SELECT a FROM Actor a WHERE a.tmdbId = :tmdbId",
                        Actor.class
                )
                .setParameter("tmdbId", tmdbId)
                .getResultStream()
                .findFirst()
                .orElse(null);

        em.close();

        return actor;
    }
}