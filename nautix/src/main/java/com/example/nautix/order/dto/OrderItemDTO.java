package com.example.nautix.order.dto;



import lombok.Data;

@Data
public class OrderItemDTO {
    private String imageUrl;
    private Long productItemId;      
    private String productName;
    private String size;
    private int quantity;
    private double priceAtOrderTime;
    private double lineTotal;
    private String color;

}

