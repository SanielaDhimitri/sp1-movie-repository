package dk.ek.dto.tmdb;//DTO-data som vi henter fra tmdb

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)// TMDb har mange felter, men vi tager kun id og name.
public record ActorDTO(

        @JsonProperty("id")//Jackson annotation siger=tager id fra json i tmdb og læg to dto-s id
        long id,               //JSON → DTO.

        @JsonProperty("name")
        String name
) {
}
//ActorDTO  ==   Actor Entity
//ActorDTO repræsenterer én actor.
