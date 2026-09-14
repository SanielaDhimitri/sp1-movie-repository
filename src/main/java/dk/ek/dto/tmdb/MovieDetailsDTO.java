package dk.ek.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MovieDetailsDTO(
        long id,
        String title,
        List<GenreDTO> genres
) {
}