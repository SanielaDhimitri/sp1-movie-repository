package dk.ek.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

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
    @Column(
            name = "tmdb_id",
            unique = true,
            nullable = false
    )
    private Long tmdbId;

    @Column(
            name = "name",
            nullable = false,
            length = 255
    )
    private String name;

    @ManyToMany(mappedBy = "actors")
    private Set<Movie> movies = new HashSet<>();

    public Actor(Long tmdbId, String name) {
        this.tmdbId = tmdbId;
        this.name = name;
    }
}