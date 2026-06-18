package com.rayaine.ecommerce.dto;

import java.util.Map;

public class PlaceOrderRequest {

    private Map<Long,Integer> selectedProducts;
    private String destination;

    public PlaceOrderRequest(){}


    public PlaceOrderRequest( Map<Long,Integer> selectedProducts , String destination){
        this.selectedProducts = selectedProducts;
        this.destination = destination;
    }


    public Map<Long, Integer> getSelectedProducts() {
        return selectedProducts;
    }

    public void setSelectedProducts(Map<Long, Integer> selectedProducts) {
        this.selectedProducts = selectedProducts;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
