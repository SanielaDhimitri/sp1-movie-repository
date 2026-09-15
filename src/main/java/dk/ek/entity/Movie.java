package dk.ek.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "movie")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(
            name = "tmdb_id",
            unique = true,
            nullable = false
    )
    private Long tmdbId;

    @Column(
            name = "title",
            nullable = false,
            length = 255
    )
    private String title;


    private LocalDate releaseDate;


    private Double rating;


    private Double popularity;


    // Movie <-> Actor
    @ManyToMany
    @JoinTable(
            name = "movie_actor",
            joinColumns = @JoinColumn(
                    name = "movie_id",
                    nullable = false
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "actor_id",
                    nullable = false
            )
    )
    private Set<Actor> actors = new HashSet<>();


    // Movie -> Director
    @ManyToOne
    @JoinColumn(name = "director_id")
    private Director director;


    // Movie <-> Genre
    @ManyToMany
    @JoinTable(
            name = "movie_genre",
            joinColumns = @JoinColumn(
                    name = "movie_id",
                    nullable = false
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "genre_id",
                    nullable = false
            )
    )
    private Set<Genre> genres = new HashSet<>();


    public Movie(
            Long tmdbId,
            String title,
            LocalDate releaseDate,
            Double rating,
            Double popularity
    ) {
        this.tmdbId = tmdbId;
        this.title = title;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.popularity = popularity;
    }
}