package dk.ek.dto.dbresponse;

import java.util.List;

public record DirectorResponseDTO(
        Long id,
        Long tmdbId,
        String name,
        List<String> movies
) {
}