package com.rayaine.ecommerce.dto;


import com.rayaine.ecommerce.model.OrderItem;
import com.rayaine.ecommerce.model.Product;


public class OrderItemDto {

    private Long itemId;
    private int quantity;
    private double priceAtPurchase;
    private Product product;

    public OrderItemDto(OrderItem orderItem){
        this.itemId = orderItem.getItemId();
        this.quantity = orderItem.getQuantity();
        this.product = orderItem.getProduct();
        this.priceAtPurchase = orderItem.getPriceAtPurchase();
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
