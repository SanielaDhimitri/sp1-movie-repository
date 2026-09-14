package dk.ek.service;

import dk.ek.dao.ActorDAO;
import dk.ek.dto.tmdb.ActorDTO;
import dk.ek.dto.response.ActorResponseDTO;
import dk.ek.entity.Actor;
import dk.ek.entity.Movie;

import java.util.List;

public class ActorService {

    private final ActorDAO actorDAO;

    public ActorService(ActorDAO actorDAO) {
        this.actorDAO = actorDAO;
    }


    // CREATE
    public ActorResponseDTO createActor(ActorDTO actorDTO) {

        Actor actor = new Actor(
                actorDTO.id(),
                actorDTO.name()
        );

        actorDAO.create(actor);

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
            return null;
        }

        return toDTO(actor);
    }


    // READ - Finder en actor via TMDb-id
    public ActorResponseDTO getActorByTmdbId(Long tmdbId) {

        Actor actor = actorDAO.findByTmdbId(tmdbId);

        if (actor == null) {
            return null;
        }

        return toDTO(actor);
    }


    // UPDATE
    public ActorResponseDTO updateActor(Long id, ActorDTO actorDTO) {

        Actor actor = actorDAO.findById(id);

        if (actor == null) {
            return null;
        }

        actor.setTmdbId(actorDTO.id());
        actor.setName(actorDTO.name());

        Actor updatedActor = actorDAO.update(actor);

        return toDTO(updatedActor);
    }


    // DELETE
    public boolean deleteActor(Long id) {

        Actor actor = actorDAO.findById(id);

        if (actor == null) {
            return false;
        }

        actorDAO.delete(id);

        return true;
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