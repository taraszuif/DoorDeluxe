package me.zuif.doordeluxe.service;

import me.zuif.doordeluxe.dto.RatingDTO;
import me.zuif.doordeluxe.model.Rating;
import me.zuif.doordeluxe.model.door.Door;

import java.util.List;
import java.util.Optional;

public interface IRatingService {
    void save(Rating rating);

    int countAllByDoorAndRate(Door door, int rate);

    List<Rating> findAllByDoor(Door door);

    Optional<Rating> findById(String id);

    double getAverageRate(String doorId);


    void update(String id, Rating newRating);

    Optional<RatingDTO> getDTO(Door door);

    void delete(String id);
}