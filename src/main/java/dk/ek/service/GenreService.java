package dk.ek.service;

import dk.ek.dao.GenreDAO;
import dk.ek.dto.tmdb.GenreDTO;
import dk.ek.dto.response.GenreResponseDTO;
import dk.ek.entity.Genre;
import dk.ek.entity.Movie;

import java.util.List;

public class GenreService {

    private final GenreDAO genreDAO;

    public GenreService(GenreDAO genreDAO) {
        this.genreDAO = genreDAO;
    }


    // CREATE
    public GenreResponseDTO createGenre(GenreDTO genreDTO) {

        Genre genre = new Genre(
                genreDTO.id(),
                genreDTO.name()
        );

        genreDAO.create(genre);

        return toDTO(genre);
    }


    // READ - Henter alle genres fra databasen
    public List<GenreResponseDTO> getAllGenres() {

        List<Genre> genres = genreDAO.findAll();

        return genres.stream()
                .map(this::toDTO)
                .toList();
    }


    // READ - Finder en genre via database-id
    public GenreResponseDTO getGenreById(Long id) {

        Genre genre = genreDAO.findById(id);

        if (genre == null) {
            return null;
        }

        return toDTO(genre);
    }


    // READ - Finder en genre via TMDb-id
    public GenreResponseDTO getGenreByTmdbId(Long tmdbId) {

        Genre genre = genreDAO.findByTmdbId(tmdbId);

        if (genre == null) {
            return null;
        }

        return toDTO(genre);
    }


    // UPDATE
    public GenreResponseDTO updateGenre(
            Long id,
            GenreDTO genreDTO
    ) {

        Genre genre = genreDAO.findById(id);

        if (genre == null) {
            return null;
        }

        genre.setTmdbId(genreDTO.id());
        genre.setName(genreDTO.name());

        Genre updatedGenre =
                genreDAO.update(genre);

        return toDTO(updatedGenre);
    }


    // DELETE
    public boolean deleteGenre(Long id) {

        Genre genre = genreDAO.findById(id);

        if (genre == null) {
            return false;
        }

        genreDAO.delete(id);

        return true;
    }


    // ENTITY -> RESPONSE DTO
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