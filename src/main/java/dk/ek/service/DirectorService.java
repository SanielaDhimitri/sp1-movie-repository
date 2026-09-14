package dk.ek.service;

import dk.ek.dao.DirectorDAO;
import dk.ek.dto.tmdb.CrewDTO;
import dk.ek.dto.response.DirectorResponseDTO;
import dk.ek.entity.Director;
import dk.ek.entity.Movie;

import java.util.List;

public class DirectorService {

    private final DirectorDAO directorDAO;

    public DirectorService(DirectorDAO directorDAO) {
        this.directorDAO = directorDAO;
    }


    // CREATE
    public DirectorResponseDTO createDirector(CrewDTO crewDTO) {

        Director director = new Director(
                crewDTO.id(),
                crewDTO.name()
        );

        directorDAO.create(director);

        return toDTO(director);
    }


    // READ - Henter alle directors fra databasen
    public List<DirectorResponseDTO> getAllDirectors() {

        List<Director> directors = directorDAO.findAll();

        return directors.stream()
                .map(this::toDTO)
                .toList();
    }


    // READ - Finder en director via database-id
    public DirectorResponseDTO getDirectorById(Long id) {

        Director director = directorDAO.findById(id);

        if (director == null) {
            return null;
        }

        return toDTO(director);
    }


    // READ - Finder en director via TMDb-id
    public DirectorResponseDTO getDirectorByTmdbId(Long tmdbId) {

        Director director = directorDAO.findByTmdbId(tmdbId);

        if (director == null) {
            return null;
        }

        return toDTO(director);
    }


    // UPDATE
    public DirectorResponseDTO updateDirector(
            Long id,
            CrewDTO crewDTO
    ) {

        Director director = directorDAO.findById(id);

        if (director == null) {
            return null;
        }

        director.setTmdbId(crewDTO.id());
        director.setName(crewDTO.name());

        Director updatedDirector =
                directorDAO.update(director);

        return toDTO(updatedDirector);
    }


    // DELETE
    public boolean deleteDirector(Long id) {

        Director director = directorDAO.findById(id);

        if (director == null) {
            return false;
        }

        directorDAO.delete(id);

        return true;
    }


    // ENTITY -> RESPONSE DTO
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