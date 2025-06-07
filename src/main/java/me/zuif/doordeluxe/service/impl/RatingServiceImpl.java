package me.zuif.doordeluxe.service.impl;

import me.zuif.doordeluxe.dto.RatingDTO;
import me.zuif.doordeluxe.model.Rating;
import me.zuif.doordeluxe.model.door.Door;
import me.zuif.doordeluxe.repository.RatingRepository;
import me.zuif.doordeluxe.service.IRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RatingServiceImpl implements IRatingService {
    private final RatingRepository ratingRepository;

    @Autowired
    public RatingServiceImpl(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    @Override
    public void save(Rating rating) {
        ratingRepository.save(rating);
    }

    @Override
    public int countAllByDoorAndRate(Door door, int rate) {
        return ratingRepository.countAllByDoorAndRate(door, rate);
    }

    @Override
    public List<Rating> findAllByDoor(Door door) {
        return ratingRepository.findAllByDoor(door);
    }

    @Override
    public Optional<Rating> findById(String id) {
        return ratingRepository.findById(id);
    }

    @Override
    public double getAverageRate(String doorId) {
        return ratingRepository.getAverageRate(doorId);
    }

    @Override
    public void update(String id, Rating newRating) {
        Optional<Rating> foundOptional = ratingRepository.findById(id);
        if (foundOptional.isPresent()) {
            Rating found = foundOptional.get();
            found.setRate(newRating.getRate());
            found.setComment(newRating.getComment());
            found.setTitle(newRating.getTitle());
        }
        save(newRating);
    }

    @Override
    public Optional<RatingDTO> getDTO(Door door) {
        if (!ratingRepository.existsByDoor(door)) return Optional.empty();
        RatingDTO result = new RatingDTO();
        result.setDoor(door);
        List<Rating> ratings = findAllByDoor(door);
        result.setRatingList(ratings);
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            map.put(i, countAllByDoorAndRate(door, i));
        }
        result.setAverage(getAverageRate(door.getId()));
        result.setRateTotalCount(map);
        result.setRatingsCount(ratings.size());
        return Optional.of(result);
    }

    @Override
    public void delete(String id) {
        if (ratingRepository.existsById(id)) {
            ratingRepository.delete(ratingRepository.findById(id).get());
        }
    }

}
