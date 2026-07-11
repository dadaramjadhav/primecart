package com.primecart.repository;

import com.primecart.entity.Order;
import com.primecart.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrder(Order order);

    void deleteByOrder(Order order);
}