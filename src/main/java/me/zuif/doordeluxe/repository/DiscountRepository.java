package me.zuif.doordeluxe.repository;

import me.zuif.doordeluxe.model.Discount;
import me.zuif.doordeluxe.model.door.Door;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiscountRepository extends JpaRepository<Discount, Long> {
    Optional<Discount> findByDoor(Door door);
}