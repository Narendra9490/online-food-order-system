package com.foodorder.system.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {

    @NotBlank(message = "Customer name is required")
    @Size(min = 2, max = 50, message = "Customer name must be between 2 and 50 characters")
    private String customerName;

    @NotBlank(message = "Customer phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone number must be a valid 10-digit Indian mobile number")
    private String customerPhone;

    @NotBlank(message = "Delivery address is required")
    @Size(max = 255, message = "Delivery address cannot exceed 255 characters")
    private String deliveryAddress;

    @NotBlank(message = "Food item cannot be empty")
    @Size(min = 2, max = 100, message = "Food item must be between 2 and 100 characters")
    private String foodItem;

    @NotNull(message = "Quantity must be provided")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 50, message = "Maximum allowed quantity per order is 50")
    private Integer quantity;

    @NotNull(message = "Price is mandatory")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    @Digits(integer = 8, fraction = 2, message = "Price format must be up to 8 digits with 2 decimals")
    private BigDecimal price;

    @Size(max = 200, message = "Special instructions cannot exceed 200 characters")
    private String specialInstructions;
}