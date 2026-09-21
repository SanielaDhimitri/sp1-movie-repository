package dk.ek.service;

import dk.ek.dao.DirectorDAO;
import dk.ek.dto.tmdb.CrewDTO;
import dk.ek.dto.dbresponse.DirectorResponseDTO;
import dk.ek.entity.Director;
import dk.ek.entity.Movie;
import dk.ek.exceptions.ApiException;

import java.util.List;

public class DirectorService {

    private final DirectorDAO directorDAO;

    public DirectorService(DirectorDAO directorDAO) {
        this.directorDAO = directorDAO;
    }


    // =========================================================
    // CREATE
    // =========================================================

    // Modtager CrewDTO,
    // konverterer DTO til Entity og gemmer Director i databasen.
    public DirectorResponseDTO createDirector(CrewDTO crewDTO) {

        // Tjekker om director allerede findes i databasen.
        Director existingDirector =
                directorDAO.findByTmdbId(crewDTO.id());

        if (existingDirector != null) {
            return toDTO(existingDirector);
        }

        // DTO -> Entity
        Director director = toEntity(crewDTO);

        // Gemmer Director Entity i databasen.
        directorDAO.create(director);

        // Entity -> ResponseDTO
        return toDTO(director);
    }


    // =========================================================
    // READ ALL
    // =========================================================

    public List<DirectorResponseDTO> getAllDirectors() {

        List<Director> directors =
                directorDAO.findAll();

        return directors.stream()
                .map(this::toDTO)
                .toList();
    }


    // =========================================================
    // READ BY DATABASE ID
    // =========================================================

    public DirectorResponseDTO getDirectorById(Long id) {

        Director director =
                directorDAO.findById(id);

        if (director == null) {
            throw new ApiException(
                    404,
                    "Director with id " + id + " was not found in the database."
            );
        }

        return toDTO(director);
    }


    // =========================================================
    // READ BY TMDB ID
    // =========================================================

    public DirectorResponseDTO getDirectorByTmdbId(Long tmdbId) {

        Director director =
                directorDAO.findByTmdbId(tmdbId);

        if (director == null) {
            throw new ApiException(
                    404,
                    "Director with TMDb id " + tmdbId + " was not found in the database."
            );
        }

        return toDTO(director);
    }


    // =========================================================
// UPDATE
// =========================================================

    public DirectorResponseDTO updateDirector(
            Long id,
            CrewDTO crewDTO
    ) {

        Director director =
                directorDAO.findById(id);

        if (director == null) {
            throw new ApiException(
                    404,
                    "Director with id " + id + " was not found in the database."
            );
        }

        director.setTmdbId(crewDTO.id());
        director.setName(crewDTO.name());

        // Gemmer ændringerne i databasen
        directorDAO.update(director);

        // Henter director igen med movies
        Director updatedDirector =
                directorDAO.findById(id);

        return toDTO(updatedDirector);
    }

    // =========================================================
    // DELETE
    // =========================================================

    public boolean deleteDirector(Long id) {

        Director director =
                directorDAO.findById(id);

        if (director == null) {
            throw new ApiException(
                    404,
                    "Director with id " + id + " was not found in the database."
            );
        }

        directorDAO.delete(id);

        return true;
    }


    // =========================================================
    // DTO -> ENTITY
    // =========================================================

    private Director toEntity(CrewDTO crewDTO) {

        return new Director(
                crewDTO.id(),
                crewDTO.name()
        );
    }


    // =========================================================
    // ENTITY -> RESPONSE DTO
    // =========================================================

    private DirectorResponseDTO toDTO(Director director) {

        List<String> movies = director.getMovies()
                .stream()
                .map(Movie::getTitle)
                .toList();

        return new DirectorResponseDTO(
                director.getId(),
                director.getTmdbId(),
                director.getName(),
                movies
        );
    }
}