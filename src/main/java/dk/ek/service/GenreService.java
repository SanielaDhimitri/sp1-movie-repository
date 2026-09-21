package dk.ek.service;

import dk.ek.dao.GenreDAO;
import dk.ek.dto.tmdb.GenreDTO;
import dk.ek.dto.dbresponse.GenreResponseDTO;
import dk.ek.entity.Genre;
import dk.ek.entity.Movie;
import dk.ek.exceptions.ApiException;

import java.util.List;

public class GenreService {

    private final GenreDAO genreDAO;

    public GenreService(GenreDAO genreDAO) {
        this.genreDAO = genreDAO;
    }


    // =========================================================
    // CREATE
    // =========================================================

    // Modtager GenreDTO,
    // konverterer DTO til Entity og gemmer Genre i databasen.
    public GenreResponseDTO createGenre(GenreDTO genreDTO) {

        // Tjekker om genre allerede findes i databasen.
        Genre existingGenre =
                genreDAO.findByTmdbId(genreDTO.id());

        if (existingGenre != null) {
            return toDTO(existingGenre);
        }

        // DTO -> Entity
        Genre genre = toEntity(genreDTO);

        // Gemmer Genre Entity i databasen.
        genreDAO.create(genre);

        // Entity -> ResponseDTO
        return toDTO(genre);
    }


    // =========================================================
    // READ ALL
    // =========================================================

    public List<GenreResponseDTO> getAllGenres() {

        List<Genre> genres =
                genreDAO.findAll();

        return genres.stream()
                .map(this::toDTO)
                .toList();
    }


    // =========================================================
    // READ BY DATABASE ID
    // =========================================================

    public GenreResponseDTO getGenreById(Long id) {

        Genre genre =
                genreDAO.findById(id);

        if (genre == null) {
            throw new ApiException(
                    404,
                    "Genre with id " + id + " was not found in the database."
            );
        }

        return toDTO(genre);
    }


    // =========================================================
    // READ BY TMDB ID
    // =========================================================

    public GenreResponseDTO getGenreByTmdbId(Long tmdbId) {

        Genre genre =
                genreDAO.findByTmdbId(tmdbId);

        if (genre == null) {
            throw new ApiException(
                    404,
                    "Genre with TMDb id " + tmdbId + " was not found in the database."
            );
        }

        return toDTO(genre);
    }


// =========================================================
// UPDATE
// =========================================================

    public GenreResponseDTO updateGenre(
            Long id,
            GenreDTO genreDTO
    ) {

        Genre genre =
                genreDAO.findById(id);

        if (genre == null) {
            throw new ApiException(
                    404,
                    "Genre with id " + id + " was not found in the database."
            );
        }

        genre.setTmdbId(genreDTO.id());
        genre.setName(genreDTO.name());

        // Gemmer ændringerne i databasen
        genreDAO.update(genre);

        // Henter genre igen med movies
        Genre updatedGenre =
                genreDAO.findById(id);

        return toDTO(updatedGenre);
    }


    // =========================================================
    // DELETE
    // =========================================================

    public boolean deleteGenre(Long id) {

        Genre genre =
                genreDAO.findById(id);

        if (genre == null) {
            throw new ApiException(
                    404,
                    "Genre with id " + id + " was not found in the database."
            );
        }

        genreDAO.delete(id);

        return true;
    }


    // =========================================================
    // DTO -> ENTITY
    // =========================================================

    private Genre toEntity(GenreDTO genreDTO) {

        return new Genre(
                genreDTO.id(),
                genreDTO.name()
        );
    }


    // =========================================================
    // ENTITY -> RESPONSE DTO
    // =========================================================

    private GenreResponseDTO toDTO(Genre genre) {

        List<String> movies = genre.getMovies()
                .stream()
                .map(Movie::getTitle)
                .toList();

        return new GenreResponseDTO(
                genre.getId(),
                genre.getTmdbId(),
                genre.getName(),
                movies
        );
    }
}