package me.zuif.doordeluxe.service.impl;

import me.zuif.doordeluxe.model.door.Door;
import me.zuif.doordeluxe.model.door.DoorType;
import me.zuif.doordeluxe.repository.DoorRepository;
import me.zuif.doordeluxe.service.IDoorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoorServiceImpl implements IDoorService {
    private final DoorRepository doorRepository;

    @Autowired
    public DoorServiceImpl(DoorRepository doorRepository) {
        this.doorRepository = doorRepository;
    }

    @Override
    public void save(Door door) {
        door.updateDiscountedPrice(false);
        doorRepository.save(door);
    }

    @Override
    public Page<Door> findFilteredDoors(String search, List<DoorType> doorTypes, List<String> manufacturers, Double priceMin, Double priceMax, Pageable pageable) {
        if (doorTypes != null && doorTypes.isEmpty()) {
            doorTypes = null;
        }
        if (manufacturers != null && manufacturers.isEmpty()) {
            manufacturers = null;
        }
        return doorRepository.findFiltered(search, doorTypes, manufacturers, priceMin, priceMax, pageable);
    }

    @Override
    public void saveAll(List<Door> doors) {
        doorRepository.saveAll(doors);
    }

    @Override
    public void flush() {
        doorRepository.flush();
    }


    public Page<Door> findAll(PageRequest request) {
        return doorRepository.findAll(request);
    }

    @Override
    public void update(String id, Door newDoor) {
        Optional<Door> foundOptional = doorRepository.findById(id);
        if (foundOptional.isPresent()) {
            Door found = foundOptional.get();
            found.setName(newDoor.getName());
            found.setImageUrl(newDoor.getImageUrl());
            found.setDescription(newDoor.getDescription());
            found.setPrice(newDoor.getPrice());
            found.setCount(newDoor.getCount());
            found.setManufacturer(newDoor.getManufacturer());
            found.setDoorType(newDoor.getDoorType());
        }
        save(newDoor);
    }

    @Override
    public void delete(String id) {
        doorRepository.delete(findById(id));
    }

    @Override
    public Door findById(String id) {
        return doorRepository.findById(id).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public Page<Door> findAllByDescriptionLikeOrManufacturerLike(String description, String manufacturer, Pageable pageable) {
        return doorRepository.findAllByDescriptionLikeOrManufacturerLike(description, manufacturer, pageable);
    }


    @Override
    public Page<Door> findAll(Pageable pageable) {
        return doorRepository.findAll(pageable);
    }


    @Override
    public Page<Door> findAllByNameLike(String name, Pageable pageable) {
        return doorRepository.findAllByNameLike(name, pageable);
    }

    @Override
    public Page<Door> findAllBydoorType(DoorType doorType, Pageable pageable) {
        return doorRepository.findAllBydoorType(doorType, pageable);
    }


    @Override
    public long count() {
        return doorRepository.count();
    }
}
