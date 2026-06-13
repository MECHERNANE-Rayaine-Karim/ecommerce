package com.rayaine.ecommerce.repository;

import com.rayaine.ecommerce.model.Order;
import com.rayaine.ecommerce.model.User;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;


public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByUser(User user);

}
