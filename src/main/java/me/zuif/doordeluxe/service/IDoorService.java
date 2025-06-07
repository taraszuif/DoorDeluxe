package me.zuif.doordeluxe.service;

import me.zuif.doordeluxe.model.door.Door;
import me.zuif.doordeluxe.model.door.DoorType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IDoorService {
    void save(Door door);

    void saveAll(List<Door> door);

    void flush();

    void update(String id, Door newDoor);

    void delete(String id);

    Page<Door> findFilteredDoors(String search, List<DoorType> doorTypes, List<String> manufacturers, Double priceMin, Double priceMax, Pageable pageable);

    Page<Door> findAll(PageRequest request);

    Door findById(String id);

    Page<Door> findAllByDescriptionLikeOrManufacturerLike(String description, String manufacturer, Pageable pageable);

    Page<Door> findAll(Pageable pageable);

    Page<Door> findAllByNameLike(String name, Pageable pageable);

    Page<Door> findAllBydoorType(DoorType doorType, Pageable pageable);

    long count();
}
