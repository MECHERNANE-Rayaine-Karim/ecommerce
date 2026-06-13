package com.rayaine.ecommerce.model;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {


    public enum Status{
        AVAILABLE,OUT_OF_STOCK,DISCONTINUED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;
    @Column(nullable = false, unique = true )
    private String productName;
    private String productDescription;
    @Column(nullable = false)
    private Double productPrice;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    public Product(){


    }


    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public Double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Double productPrice) {
        this.productPrice = productPrice;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status productStatus) {
        this.status = productStatus;
    }

}
