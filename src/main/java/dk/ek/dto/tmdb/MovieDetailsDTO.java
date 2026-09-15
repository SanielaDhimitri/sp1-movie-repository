package dk.ek.dto.tmdb;// Detaljer for én movie fra TMDb, bruges bl.a. til at hente genres.

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MovieDetailsDTO(
        long id,
        String title,
        List<GenreDTO> genres
) {
}