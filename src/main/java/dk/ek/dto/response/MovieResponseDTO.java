package dk.ek.dto.response;//data som Service returner i Main/controller

import java.time.LocalDate;

public record MovieResponseDTO(
        Long id,
        Long tmdbId,
        String title,
        LocalDate releaseDate,
        Double rating,
        Double popularity
) {
}
