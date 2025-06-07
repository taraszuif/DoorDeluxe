package me.zuif.doordeluxe.service;

import me.zuif.doordeluxe.model.Discount;
import me.zuif.doordeluxe.model.door.Door;

import java.util.List;
import java.util.Optional;

public interface IDiscountService {
    Discount createDiscount(Discount discount);

    Optional<Discount> findByDoor(Door door);

    Optional<Discount> findById(Long id);


    List<Discount> getAllDiscounts();

    void deleteDiscount(Long id);
}