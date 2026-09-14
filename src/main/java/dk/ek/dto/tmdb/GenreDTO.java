package dk.ek.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GenreDTO(
        long id,
        String name
) {
}