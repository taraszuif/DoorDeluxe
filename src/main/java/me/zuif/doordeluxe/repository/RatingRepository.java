package me.zuif.doordeluxe.repository;

import me.zuif.doordeluxe.model.Rating;
import me.zuif.doordeluxe.model.door.Door;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, String> {

    List<Rating> findAllByDoor(Door door);

    Optional<Rating> findById(String id);


    @Query("select avg(r.rate)from Rating r where r.door.id=:doorId")
    double getAverageRate(String doorId);

    boolean existsByDoor(Door door);

    int countAllByDoorAndRate(Door door, int rate);
}