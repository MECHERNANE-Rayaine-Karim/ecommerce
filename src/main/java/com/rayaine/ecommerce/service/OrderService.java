package com.rayaine.ecommerce.service;


import com.rayaine.ecommerce.dto.OrderDto;
import com.rayaine.ecommerce.dto.OrderItemDto;
import com.rayaine.ecommerce.exception.*;
import com.rayaine.ecommerce.model.Order;
import com.rayaine.ecommerce.model.OrderItem;
import com.rayaine.ecommerce.model.Product;
import com.rayaine.ecommerce.model.User;
import com.rayaine.ecommerce.repository.OrderItemRepository;
import com.rayaine.ecommerce.repository.OrderRepository;
import com.rayaine.ecommerce.repository.ProductRepository;
import com.rayaine.ecommerce.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@Service
public class OrderService {

    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    public OrderService(UserRepository userRepository, OrderItemRepository orderItemRepository, OrderRepository orderRepository, ProductRepository productRepository ) {
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }


    private User getCurrentUser() {
        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(
                () -> new UsernameNotFoundException("user not found")
        );
        return user;
    }

    @PreAuthorize("hasRole('USER')")
    @Transactional
    public void placeOrder(Map<Long,Integer> selectedProducts, String destination)  {
        if(  selectedProducts == null || selectedProducts.isEmpty() ) throw new InvalidOrderOperationException("Order must contain at least on item");
        Map<Product,Integer> availableProducts = new HashMap<>();
        for(Map.Entry<Long,Integer> entry : selectedProducts.entrySet()){
            Product product = productRepository.findById(entry.getKey()).orElseThrow(()->new ProductNotFoundException("Product not found"));
            if( !product.getStatus().equals(Product.Status.AVAILABLE)) {
                throw new UnavailableProductException("Product " + product.getProductName() + " is not available");
            }
            else availableProducts.put(product,entry.getValue());
        }
        Order order = new Order();
        order.setUser(this.getCurrentUser());
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

    @Transactional
    public void cancelOrder( Long orderId ) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () ->  new OrderNotFoundException("order not found")
        );
        User user = this.getCurrentUser();
        if( !user.getUserId().equals(order.getUser().getUserId())) throw new UnauthorizedOrderAccessException("invalid operation");
        if( !order.getStatus().equals(Order.Status.PENDING) ){
            throw new InvalidOrderOperationException("invalid operation, order is already " + String.valueOf(order.getStatus()));
        }
        if( !order.getCreatedAt().plusHours(30).isAfter(LocalDateTime.now())){
            throw new InvalidOrderOperationException("invalid operation, time allowed for cancelation is expire ");
        }
        order.setStatus(Order.Status.CANCELLED);
        orderRepository.save(order);
    }

    public Page<OrderDto> getOrders(Order.Status status, Pageable pageable) {
        User user = this.getCurrentUser();
        Specification<Order> specification = (root, query, criteriaBuilder ) -> criteriaBuilder.conjunction();
        if( status != null ){
            specification = specification.and((root,query,cb)->cb.equal(root.get("status"),status));
        }
        specification = specification.and((root,query,cb)->cb.equal(root.get("user"),user));
        List<OrderDto> ordersList = new ArrayList<>();
        for(Order order : orderRepository.findAll(specification,pageable) ){
            ordersList.add(new OrderDto(order));
        }
        return new PageImpl<>(ordersList,PageRequest.of(0,10),ordersList.size());
    }

    public OrderDto getOrderDetails( Long orderId ) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () ->  new OrderNotFoundException("order not found")
        );
        User user = this.getCurrentUser();
        if( !user.getUserId().equals(order.getUser().getUserId())) {
            throw new UnauthorizedOrderAccessException("invalid operation");
        }
        List<OrderItemDto> orderItemDtoList = new ArrayList<>();
        for( OrderItem orderItem : orderItemRepository.findByOrder(order)){
            orderItemDtoList.add(new OrderItemDto(orderItem));
        }
        OrderDto orderDto = new OrderDto(order,orderItemDtoList);
        return orderDto;
    }
}
