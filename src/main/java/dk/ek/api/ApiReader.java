package dk.ek.api;

import dk.ek.dto.tmdb.CreditsDTO;
import dk.ek.dto.tmdb.GenreResultDTO;
import dk.ek.dto.tmdb.MovieDetailsDTO;
import dk.ek.dto.tmdb.MovieResultDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;


public class ApiReader {
    private final String apiKey;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ApiReader(String apiKey) {
        this.apiKey = apiKey;
    }

    public String readAPI(String url, String searchQuery, String year) {
        try {
            String buildUrl = url + "?api_key=" + apiKey + "&query=" + searchQuery + "&year=" + year;
            // Create an HttpClient instance
            HttpClient client = HttpClient.newHttpClient();

            // Create a request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(buildUrl))
                    .GET()
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Check the status code and print the response
            if (response.statusCode() != 200) {
                throw new RuntimeException("GET request failed. Status code: " + response.statusCode());
            }

            return response.body();  //SON-i som TMDb returner

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }


    }



    // Henter alle danske movies fra de sidste 5 år fra TMDb.
    public String discoverMovies(String url, int page) {

        LocalDate today = LocalDate.now();
        LocalDate fiveYearsAgo = today.minusYears(5);

        String buildUrl = url
                + "?api_key=" + apiKey
                + "&with_origin_country=DK"
                + "&primary_release_date.gte=" + fiveYearsAgo
                + "&primary_release_date.lte=" + today
                + "&page=" + page;

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(buildUrl))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (response.statusCode() != 200) {
                throw new RuntimeException(
                        "GET request failed. Status code: "
                                + response.statusCode()
                );
            }

            return response.body();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Henter actors og crew for en movie fra TMDb.
    public String getMovieCredits(String url, long movieId) {

        String buildUrl =
                url + "/" + movieId + "/credits?api_key=" + apiKey;

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(buildUrl))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException(
                        "GET request failed. Status code: " + response.statusCode()
                );
            }

            return response.body();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public MovieResultDTO convertFromJson(String json) {
        try {
            MovieResultDTO movieResultDTO =
                    objectMapper.readValue(json, MovieResultDTO.class);

            return movieResultDTO;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // Konverterer credits JSON til CreditsDTO med Jackson.
    public CreditsDTO convertCreditsFromJson(String json) {
        try {
            return objectMapper.readValue(json, CreditsDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public String getGenres(String url) {

        String buildUrl = url + "?api_key=" + apiKey;

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(buildUrl))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException(
                        "GET request failed. Status code: " + response.statusCode()
                );
            }

            return response.body();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public GenreResultDTO convertGenresFromJson(String json) {
        try {
            return objectMapper.readValue(json, GenreResultDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    // Henter detaljer for én movie fra TMDb.
    public String getMovieDetails(String url, long movieId) {

        String buildUrl =
                url + "/" + movieId + "?api_key=" + apiKey;

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(buildUrl))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException(
                        "GET request failed. Status code: " + response.statusCode()
                );
            }

            return response.body();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public MovieDetailsDTO convertMovieDetailsFromJson(String json) {
        try {
            return objectMapper.readValue(json, MovieDetailsDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
//from template/kod of profesor
// ApiReader henter JSON-data fra TMDb API
// og konverterer JSON til MovieResultDTO med Jackson.
//ApiReader/Client bruges til at kommunikere med et API og hente data.(for alle project)

