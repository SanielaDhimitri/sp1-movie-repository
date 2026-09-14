package dk.ek.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ActorDTO(

        @JsonProperty("id")
        long id,

        @JsonProperty("name")
        String name
) {
}
//ActorDTO  ==   Actor Entity
//ActorDTO repræsenterer én actor.
