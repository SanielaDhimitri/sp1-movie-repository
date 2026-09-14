package dk.ek.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "actor")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Actor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    @Column(name = "tmdb_id", unique = true)
    private Long tmdbId;


    private String name;
    @ManyToMany(mappedBy = "actors")
    private Set<Movie> movies = new HashSet<>();

    public Actor(Long tmdbId, String name) {
        this.tmdbId = tmdbId;
        this.name = name;
    }
}