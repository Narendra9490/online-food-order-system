package com.foodorder.system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder     //More readable, and you can omit fields that don't need values yet.
public class Order {
    private Long orderId;
    private String customerName;
    private String customerPhone;
    private String deliveryAddress;
    private String foodItem;
    private Integer quantity;
    private BigDecimal price;
    private OrderStatus orderStatus;
    private String specialInstructions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}