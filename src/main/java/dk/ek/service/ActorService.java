package dk.ek.service;

import dk.ek.dao.ActorDAO;
import dk.ek.dto.tmdb.ActorDTO;
import dk.ek.dto.dbresponse.ActorResponseDTO;
import dk.ek.entity.Actor;
import dk.ek.entity.Movie;
import dk.ek.exceptions.ApiException;

import java.util.List;

public class ActorService {

    private final ActorDAO actorDAO;

    public ActorService(ActorDAO actorDAO) {
        this.actorDAO = actorDAO;
    }


    // CREATE
// Modtager ActorDTO,
// konverterer DTO til Entity og gemmer Actor i databasen.
    public ActorResponseDTO createActor(ActorDTO actorDTO) {

        // Tjekker om actor allerede findes i databasen.
        Actor existingActor =
                actorDAO.findByTmdbId(actorDTO.id());

        if (existingActor != null) {
            return toDTO(existingActor);
        }

        // DTO -> Entity
        Actor actor = toEntity(actorDTO);

        // Gemmer Actor Entity i databasen.
        actorDAO.create(actor);

        // Entity -> ResponseDTO
        return toDTO(actor);
    }


    // READ - Henter alle actors fra databasen
    public List<ActorResponseDTO> getAllActors() {

        List<Actor> actors = actorDAO.findAll();

        return actors.stream()
                .map(this::toDTO)
                .toList();
    }


    // READ - Finder en actor via database-id
    public ActorResponseDTO getActorById(Long id) {

        Actor actor = actorDAO.findById(id);

        if (actor == null) {
            throw new ApiException(
                    404,
                    "Actor with id " + id + " was not found in the database."
            );

        }

        return toDTO(actor);
    }


    // READ - Finder en actor via TMDb-id
    public ActorResponseDTO getActorByTmdbId(Long tmdbId) {

        Actor actor = actorDAO.findByTmdbId(tmdbId);

        if (actor == null) {
            throw new ApiException(
                    404,
                    "Actor with TMDb id " + tmdbId + " was not found in the database."
            );
        }

        return toDTO(actor);
    }

    // UPDATE
    public ActorResponseDTO updateActor(Long id, ActorDTO actorDTO) {

        Actor actor = actorDAO.findById(id);

        if (actor == null) {
            throw new ApiException(
                    404,
                    "Actor with id " + id + " was not found in the database."
            );
        }

        actor.setTmdbId(actorDTO.id());
        actor.setName(actorDTO.name());

        actorDAO.update(actor);

        // Henter actor igen med movies
        Actor updatedActor = actorDAO.findById(id);

        return toDTO(updatedActor);
    }



    // DELETE
    public boolean deleteActor(Long id) {

        Actor actor = actorDAO.findById(id);

        if (actor == null) {
            throw new ApiException(
                    404,
                    "Actor with id " + id + " was not found in the database."
            );
        }

        actorDAO.delete(id);

        return true;
    }
    // DTO -> ENTITY
    private Actor toEntity(ActorDTO actorDTO) {

        return new Actor(
                actorDTO.id(),
                actorDTO.name()
        );
    }

    // ENTITY -> RESPONSE DTO
    private ActorResponseDTO toDTO(Actor actor) {

        List<String> movies = actor.getMovies()
                .stream()
                .map(Movie::getTitle)
                .toList();

        return new ActorResponseDTO(
                actor.getId(),
                actor.getTmdbId(),
                actor.getName(),
                movies
        );
    }
}

// ApiReader henter JSON-data og konverterer dem til DTO'er med Jackson.
// Service modtager DTO'erne og konverterer dem til Entities.
// DAO bruger Entities til at gemme data i databasen.
//DAO komunikon me PostgreSQL përmes JPA/Hibernate