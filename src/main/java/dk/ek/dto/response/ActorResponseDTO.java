package dk.ek.dto.response;// når vi læser fra db

import java.util.List;

public record ActorResponseDTO(
        Long id,
        Long tmdbId,
        String name,
        List<String> movies
) {
}