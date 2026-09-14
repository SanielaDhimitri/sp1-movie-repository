package dk.ek.dto.response;

import java.util.List;

public record GenreResponseDTO(
        Long id,
        Long tmdbId,
        String name,
        List<String> movies
) {
}