package me.zuif.doordeluxe.dto;

import lombok.Data;
import me.zuif.doordeluxe.model.order.Order;
import me.zuif.doordeluxe.model.order.OrderDetails;

import java.util.Map;

@Data
public class OrderDTO {
    private Order order;
    private Map<String, OrderDetails> detailsMap;
}
