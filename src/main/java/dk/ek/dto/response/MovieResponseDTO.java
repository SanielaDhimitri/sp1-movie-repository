package dk.ek.dto.response;

import java.time.LocalDate;
import java.util.List;

public record MovieResponseDTO(
        Long id,
        Long tmdbId,
        String title,
        LocalDate releaseDate,
        Double rating,
        Double popularity,
        List<String> actors,
        String director,
        List<String> genres
) {
}