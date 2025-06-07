package me.zuif.doordeluxe.service.impl;

import me.zuif.doordeluxe.model.door.Door;
import me.zuif.doordeluxe.model.order.Order;
import me.zuif.doordeluxe.model.order.OrderDetails;
import me.zuif.doordeluxe.model.order.OrderStatus;
import me.zuif.doordeluxe.service.ICartService;
import me.zuif.doordeluxe.service.IDoorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CartServiceImpl implements ICartService {
    private final Map<String, Integer> cart = new LinkedHashMap<>();
    private final IDoorService doorService;

    @Autowired
    public CartServiceImpl(IDoorService doorService) {
        this.doorService = doorService;
    }

    @Override
    public void addDoor(String id) {

        if (cart.containsKey(id)) {
            cart.put(id, cart.get(id) + 1);
        } else {
            cart.put(id, 1);
        }

    }

    @Override
    public int getTotalCount() {
        return cart.values().stream().mapToInt(Integer::intValue).sum();
    }

    public Map<String, Integer> rawCart() {
        return cart;
    }

    @Override
    public void removeDoor(String id) {
        if (cart.containsKey(id)) {
            if (cart.get(id) > 1)
                cart.replace(id, cart.get(id) - 1);
            else if (cart.get(id) == 1) {
                cart.remove(id);
            }
        }
    }

    @Override
    public void clear() {
        cart.clear();
    }

    @Override
    public Map<Door, Integer> getCart() {
        return Collections.unmodifiableMap(cart.entrySet().stream().collect(
                Collectors.toMap(e -> doorService.findById(e.getKey()), e -> e.getValue())));
    }

    @Override
    public BigDecimal totalPrice() {
        return cart.entrySet().stream()
                .map(entry -> {
                    String doorId = entry.getKey();
                    int quantity = entry.getValue();
                    Door door = doorService.findById(doorId);

                    BigDecimal price = (door.getDiscount() != null)
                            ? door.getPriceWithDiscountRaw()
                            : door.getPrice();

                    return price.multiply(BigDecimal.valueOf(quantity));
                })
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public Optional<Order> checkout() {
        Order result = new Order();
        result.setTotalPrice(totalPrice());
        List<OrderDetails> orderDetails = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : cart.entrySet()) {
            Door door = doorService.findById(entry.getKey());
            if (door.getCount() < entry.getValue())
                return Optional.empty();
            OrderDetails details = new OrderDetails();
            details.setDoor(door);
            details.setCount(entry.getValue());
            details.setOrder(result);
            orderDetails.add(details);
            doorService.findById(entry.getKey()).setCount(door.getCount() - entry.getValue());
        }
        List<Door> cartSet = cart.keySet().stream().map(s -> doorService.findById(s)).collect(Collectors.toList());
        doorService.saveAll(new ArrayList<>(cartSet));
        doorService.flush();
        result.setDoors(cartSet);
        result.setDetails(orderDetails);
        result.setStatus(OrderStatus.CREATED);
        cart.clear();
        return Optional.of(result);

    }

}
