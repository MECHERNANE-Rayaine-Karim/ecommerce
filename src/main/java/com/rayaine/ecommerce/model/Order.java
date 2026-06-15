package com.rayaine.ecommerce.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "orders")
public class Order {




    public enum Status{
        PENDING,DELIVERED,CANCELLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime estimatedArrival;
    @Column(nullable = false)
    private String destination;
    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false,updatable = false)
    private User user;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    public Order(){

    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long odrerId) {
        this.orderId = odrerId;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
