package dk.ek.config;

import dk.ek.entity.Actor;
import dk.ek.entity.Director;
import dk.ek.entity.Genre;
import dk.ek.entity.Movie;
import org.hibernate.cfg.Configuration;

final class EntityRegistry {

    private EntityRegistry() {}

    static void registerEntities(Configuration configuration) {

        configuration.addAnnotatedClass(Movie.class);
        configuration.addAnnotatedClass(Actor.class);
        configuration.addAnnotatedClass(Director.class);
        configuration.addAnnotatedClass(Genre.class);
    }
}