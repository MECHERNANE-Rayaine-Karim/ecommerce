package com.rayaine.ecommerce.service;


import com.rayaine.ecommerce.model.Order;
import com.rayaine.ecommerce.model.OrderItem;
import com.rayaine.ecommerce.model.Product;
import com.rayaine.ecommerce.repository.OrderItemRepository;
import com.rayaine.ecommerce.repository.OrderRepository;
import com.rayaine.ecommerce.repository.ProductRepository;
import com.rayaine.ecommerce.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class OrderService {

    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    public OrderService(UserRepository userRepository, OrderItemRepository orderItemRepository, OrderRepository orderRepository, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }


    @Transactional
    public void placeOrder(Map<Long,Integer> selectedProducts, String destination) throws Exception {
        if(  selectedProducts == null || selectedProducts.isEmpty() ) throw new Exception("Order must contain at least on item");
        Map<Product,Integer> availableProducts = new HashMap<>();
        for(Map.Entry<Long,Integer> entry : selectedProducts.entrySet()){
            Product product = productRepository.findById(entry.getKey()).orElseThrow(()->new Exception("Product not found"));
            if( !product.getStatus().equals(Product.Status.AVAILABLE)) {
                throw new Exception("Product " + product.getProductName() + " is not available");
            }
            else availableProducts.put(product,entry.getValue());
        }
        Order order = new Order();
        order.setUser(userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(
                () -> new Exception("user not found")
        ));
        order.setDestination(destination);
        order.setCreatedAt(LocalDateTime.now());
        order.setEstimatedArrival(LocalDateTime.now().plusDays(5));
        order.setStatus(Order.Status.PENDING);
        orderRepository.save(order);
        for(Map.Entry<Product,Integer> entry : availableProducts.entrySet()){
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(entry.getKey());
            orderItem.setQuantity(entry.getValue());
            orderItem.setPriceAtPurchase(entry.getKey().getProductPrice());
            orderItem.setOrder(order);
            orderItemRepository.save(orderItem);
        }
    }
}
