package dk.ek.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CrewDTO(

        @JsonProperty("id")
        long id,

        @JsonProperty("name")
        String name,

        @JsonProperty("job")
        String job
) {
}
// CrewDTO represents one crew member.
// The job field is used to find the Director.