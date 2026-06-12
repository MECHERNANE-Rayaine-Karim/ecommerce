package com.rayaine.ecommerce.model;

import jakarta.persistence.*;
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;
    @Column(nullable = false,updatable = false)
    private int quantity;
    @Column(nullable = false,updatable = false)
    private double priceAtPurchase;
    @ManyToOne
    @JoinColumn(name = "order_id",nullable = false,updatable = false)
    private Order order;
    @ManyToOne
    @JoinColumn(name = "product_id",nullable = false,updatable = false)
    private Product product;

    public OrderItem(){

    }


    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPriceAtPurchase() {
        return priceAtPurchase;
    }

    public void setPriceAtPurchase(double priceAtPurchase) {
        this.priceAtPurchase = priceAtPurchase;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
