package com.rayaine.ecommerce.dto;

import com.rayaine.ecommerce.model.Order;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {

    private Long orderId;
    private LocalDateTime createdAt;
    private LocalDateTime estimatedArrival;
    private String destination;
    private Order.Status status;
    private List<OrderItemDto> orderItemList;


    public OrderDto(Order order, List<OrderItemDto> orderItemList){
        this.orderId = order.getOrderId();
        this.status = order.getStatus();
        this.destination = order.getDestination();
        this.createdAt = order.getCreatedAt();
        this.estimatedArrival = order.getEstimatedArrival();
        this.orderItemList = orderItemList;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getEstimatedArrival() {
        return estimatedArrival;
    }

    public void setEstimatedArrival(LocalDateTime estimatedArrival) {
        this.estimatedArrival = estimatedArrival;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Order.Status getStatus() {
        return status;
    }

    public void setStatus(Order.Status status) {
        this.status = status;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public List<OrderItemDto> getOrderItemList() {
        return orderItemList;
    }
}
