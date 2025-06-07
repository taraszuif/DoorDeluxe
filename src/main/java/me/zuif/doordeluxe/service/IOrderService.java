package me.zuif.doordeluxe.service;

import me.zuif.doordeluxe.dto.OrderDTO;
import me.zuif.doordeluxe.model.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

public interface IOrderService {
    void save(Order order);

    void delete(String id);

    Page<Order> findAll(PageRequest request);

    Map<LocalDate, BigDecimal> getTotalRevenueByDate(LocalDate start, LocalDate end);

    Map<LocalDate, Long> getOrderCountByDate(LocalDate start, LocalDate end);

    Optional<OrderDTO> getDTO(String id);

    Page<Order> findAllByUserId(String userId, Pageable pageable);

    Optional<Order> findById(String id);

}
