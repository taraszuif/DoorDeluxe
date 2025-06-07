package me.zuif.doordeluxe.service.impl;

import me.zuif.doordeluxe.dto.OrderDTO;
import me.zuif.doordeluxe.model.door.Door;
import me.zuif.doordeluxe.model.order.Order;
import me.zuif.doordeluxe.model.order.OrderDetails;
import me.zuif.doordeluxe.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class OrderServiceImpl implements me.zuif.doordeluxe.service.IOrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void save(Order order) {
        orderRepository.save(order);
    }

    @Override
    public void delete(String id) {
        orderRepository.delete(findById(id).get());
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Order> findAll(PageRequest request) {
        Page<Order> orders = orderRepository.findAll(request);
        orders.getContent().forEach(order -> {
            order.getDoors().size();
            order.getUser().getUserName();
        });
        return orders;
    }

    @Override
    public Map<LocalDate, BigDecimal> getTotalRevenueByDate(LocalDate start, LocalDate end) {
        List<Object[]> results = orderRepository.findTotalRevenueByDate(start.atStartOfDay(), end.plusDays(1).atStartOfDay());
        Map<LocalDate, BigDecimal> revenueMap = new TreeMap<>();
        for (Object[] row : results) {
            LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
            BigDecimal total = (BigDecimal) row[1];
            revenueMap.put(date, total);
        }
        return revenueMap;
    }

    @Override
    public Map<LocalDate, Long> getOrderCountByDate(LocalDate start, LocalDate end) {
        List<Object[]> results = orderRepository.findOrderCountByDate(start.atStartOfDay(), end.plusDays(1).atStartOfDay());
        Map<LocalDate, Long> countMap = new TreeMap<>();
        for (Object[] row : results) {
            LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
            Long count = (Long) row[1];
            countMap.put(date, count);
        }
        return countMap;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<OrderDTO> getDTO(String id) {
        Optional<Order> orderOptional = findById(id);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.getDoors().forEach(Door::getId);
            OrderDTO result = new OrderDTO();
            result.setOrder(order);
            Map<String, OrderDetails> detailsMap = new HashMap<>();
            for (OrderDetails details : order.getDetails()) {
                detailsMap.put(details.getDoor().getId(), details);
            }
            result.setDetailsMap(detailsMap);
            return Optional.of(result);
        } else {
            return Optional.empty();
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Order> findAllByUserId(String userId, Pageable pageable) {
        Page<Order> page = orderRepository.findAllByUserId(userId, pageable);
        page.getContent().forEach(order -> order.getDoors().size());
        return page;
    }

    @Override
    public Optional<Order> findById(String id) {
        return orderRepository.findById(id);
    }
}
