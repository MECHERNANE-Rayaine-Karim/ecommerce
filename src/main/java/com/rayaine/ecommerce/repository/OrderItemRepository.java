package com.rayaine.ecommerce.repository;

import com.rayaine.ecommerce.model.Order;
import com.rayaine.ecommerce.model.OrderItem;
import com.rayaine.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {

    List<OrderItem> findByOrder(Order order);
    List<OrderItem> findByProduct(Product product);
}
