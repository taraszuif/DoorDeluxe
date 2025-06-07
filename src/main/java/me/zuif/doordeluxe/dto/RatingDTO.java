package me.zuif.doordeluxe.dto;

import lombok.Data;
import me.zuif.doordeluxe.model.Rating;
import me.zuif.doordeluxe.model.door.Door;
import me.zuif.doordeluxe.model.door.Door;

import java.util.List;
import java.util.Map;

@Data
public class RatingDTO {
    private Map<Integer, Integer> rateTotalCount;
    private double average;
    private Door door;
    private List<Rating> ratingList;
    private double ratingsCount;
}
