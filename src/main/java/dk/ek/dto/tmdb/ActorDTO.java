package dk.ek.dto.tmdb; // DTO-data som vi henter fra TMDb

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
// TMDb har mange felter, men vi tager kun id og name.
public record ActorDTO(

        @JsonProperty("id")//Jackson annotation.
        // Jackson tager "id" fra JSON og lægger værdien i DTO'ens id.
        long id,

        @JsonProperty("name")
        // Jackson tager "name" fra JSON og lægger værdien i DTO'ens name.
        String name
) {
}
// ActorDTO bruges til at transportere data fra TMDb.
// ActorDTO repræsenterer én actor fra TMDb.
// ActorDTO er IKKE en Entity.
