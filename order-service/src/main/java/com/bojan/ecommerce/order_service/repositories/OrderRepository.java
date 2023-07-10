package com.bojan.ecommerce.order_service.repositories;

import com.bojan.ecommerce.order_service.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findOrdersByCustomerIdOrderByUpdatedAt(String customerId);
}
