package dk.ek.dto.tmdb;//data for en movie der kommer fra TMDb

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MovieDTO(
        @JsonProperty("id")
        int id,

        @JsonProperty("title")
        String title,

        @JsonProperty("overview")
        String overView,

        @JsonProperty("release_date")
        String releaseDate,

        @JsonProperty("vote_average")
        Double rating,

        @JsonProperty("popularity")
        Double popularity
) {
}

//MovieDTO  ==   Movie Entity
//MovieDTO repræsenterer én movie.
//movieResultDTO repræsenter en list med movie