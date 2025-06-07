package me.zuif.doordeluxe.repository;

import me.zuif.doordeluxe.model.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    Page<Order> findAllByUserId(String userId, Pageable pageable);

    Optional<Order> findById(String id);

    @Query("SELECT DATE(o.addTime), SUM(o.totalPrice) FROM Order o WHERE o.addTime BETWEEN :start AND :end GROUP BY DATE(o.addTime)")
    List<Object[]> findTotalRevenueByDate(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT DATE(o.addTime), COUNT(o) FROM Order o WHERE o.addTime BETWEEN :start AND :end GROUP BY DATE(o.addTime)")
    List<Object[]> findOrderCountByDate(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}
