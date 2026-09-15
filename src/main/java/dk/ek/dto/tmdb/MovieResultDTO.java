//sider, list movie, antal sider i alt, anytal movie i alt
package dk.ek.dto.tmdb;//response fra tmdb/lista der kommer fra TMDb

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MovieResultDTO(
        @JsonProperty("page")
        int page,
        @JsonProperty("results")
        List<MovieDTO> results,
        @JsonProperty("total_pages")
        int totalPages,
        @JsonProperty("total_results")
        int totalResults
) {
}


// MovieResultDTO indeholder en liste af MovieDTO fra TMDb.
//MovieDTO repræsenterer én movie.