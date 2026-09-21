package dk.ek.dto.dbresponse;// når vi læser fra db

import java.util.List;

public record ActorResponseDTO(
        Long id,
        Long tmdbId,
        String name,
        List<String> movies
) {
}
// læser data fra db
// Service konverterer Entity til ResponseDTO

