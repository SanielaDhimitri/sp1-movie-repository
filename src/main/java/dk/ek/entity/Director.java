package dk.ek.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "director")
public class Director {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "tmdb_id", unique = true)
    private Long tmdbId;

    private String name;
    @OneToMany(mappedBy = "director")
    private Set<Movie> movies = new HashSet<>();

    public Director(Long tmdbId, String name) {
        this.tmdbId = tmdbId;
        this.name = name;
    }
}