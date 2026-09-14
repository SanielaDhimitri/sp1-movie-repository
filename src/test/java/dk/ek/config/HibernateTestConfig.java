package dk.ek.config;

import dk.ek.entity.Actor;
import dk.ek.entity.Director;
import dk.ek.entity.Genre;
import dk.ek.entity.Movie;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.cfg.Configuration;

public class HibernateTestConfig {

    public static EntityManagerFactory getEntityManagerFactory() {

        Configuration configuration = new Configuration();

        configuration.setProperty(
                "hibernate.connection.driver_class",
                "org.testcontainers.jdbc.ContainerDatabaseDriver"
        );

        configuration.setProperty(
                "hibernate.connection.url",
                "jdbc:tc:postgresql:16:///test_db"
        );

        configuration.setProperty(
                "hibernate.hbm2ddl.auto",
                "create-drop"
        );

        // Entities
        configuration.addAnnotatedClass(Movie.class);
        configuration.addAnnotatedClass(Actor.class);
        configuration.addAnnotatedClass(Director.class);
        configuration.addAnnotatedClass(Genre.class);

        return configuration.buildSessionFactory();
    }
}