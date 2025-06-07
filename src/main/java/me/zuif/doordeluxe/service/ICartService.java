package me.zuif.doordeluxe.service;

import me.zuif.doordeluxe.model.door.Door;
import me.zuif.doordeluxe.model.order.Order;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public interface ICartService {
    void addDoor(String id);

    void removeDoor(String id);

    void clear();

    int getTotalCount();

    Map<Door, Integer> getCart();

    BigDecimal totalPrice();

    Optional<Order> checkout();
}
