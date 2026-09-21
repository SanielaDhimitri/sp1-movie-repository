package dk.ek.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CreditsResultDTO(

        @JsonProperty("cast")
        List<ActorDTO> cast,

        @JsonProperty("crew")
        List<CrewDTO> crew
) {
}


