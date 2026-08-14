package com.agile.jewelryshop.repository;

import com.agile.jewelryshop.model.Order;
import com.agile.jewelryshop.model.OrderStatus;
import com.agile.jewelryshop.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<Order> findByOrderCode(String orderCode);
    Optional<Order> findByIdAndUserId(Long id, Long userId);
    List<Order> findAllByOrderByCreatedAtDesc();
    long countByStatus(OrderStatus status);
    List<Order> findByStatusAndPaymentStatus(OrderStatus status, PaymentStatus paymentStatus);

    @Query("select coalesce(sum(o.totalAmount), 0) from Order o where o.status = com.agile.jewelryshop.model.OrderStatus.COMPLETED")
    BigDecimal totalCompletedRevenue();
}
