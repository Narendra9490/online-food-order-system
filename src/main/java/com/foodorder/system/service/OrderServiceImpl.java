package com.foodorder.system.service;

import com.foodorder.system.exception.InvalidOrderStateException;
import com.foodorder.system.exception.OrderProcessingException;
import com.foodorder.system.exception.ResourceNotFoundException;
import com.foodorder.system.model.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    // Thread-safe in-memory store
    private final Map<Long, Order> orderStorage = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1000);

    @Override
    public Order createOrder(OrderCreateRequest request) throws OrderProcessingException {
        // Enforcing compile-time checked exception for high-value orders requiring manual approval
        BigDecimal orderTotal = request.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
        if (orderTotal.compareTo(new BigDecimal("100000.00")) > 0) {
            throw new OrderProcessingException("Single order value exceeds ₹1,00,000 threshold. Manual verification required.");
        }

        Long newId = idCounter.incrementAndGet();
        LocalDateTime now = LocalDateTime.now();

        Order order = Order.builder()
                .orderId(newId)
                .customerName(request.getCustomerName().trim())
                .customerPhone(request.getCustomerPhone().trim())
                .deliveryAddress(request.getDeliveryAddress().trim())
                .foodItem(request.getFoodItem().trim())
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .orderStatus(OrderStatus.PLACED)
                .specialInstructions(request.getSpecialInstructions())
                .createdAt(now)
                .updatedAt(now)
                .build();

        orderStorage.put(newId, order);
        return order;
    }

    @Override
    public List<Order> getAllOrders() {
        return orderStorage.values().stream()
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Override
    public Order getOrderById(Long orderId) {
        Order order = orderStorage.get(orderId);
        if (order == null) {
            throw new ResourceNotFoundException("Order not found with ID: " + orderId);
        }
        return order;
    }

    @Override
    public Order updateOrder(Long orderId, OrderUpdateRequest request) {
        Order existingOrder = getOrderById(orderId);

        if (existingOrder.getOrderStatus() == OrderStatus.DELIVERED || existingOrder.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new InvalidOrderStateException("Cannot update an order that is already " + existingOrder.getOrderStatus());
        }

        existingOrder.setCustomerName(request.getCustomerName().trim());
        existingOrder.setCustomerPhone(request.getCustomerPhone().trim());
        existingOrder.setDeliveryAddress(request.getDeliveryAddress().trim());
        existingOrder.setFoodItem(request.getFoodItem().trim());
        existingOrder.setQuantity(request.getQuantity());
        existingOrder.setPrice(request.getPrice());
        existingOrder.setSpecialInstructions(request.getSpecialInstructions());
        existingOrder.setUpdatedAt(LocalDateTime.now());

        orderStorage.put(orderId, existingOrder);
        return existingOrder;
    }

    @Override
    public Order patchOrder(Long orderId, OrderPatchRequest request) {
        Order existingOrder = getOrderById(orderId);

        if (existingOrder.getOrderStatus() == OrderStatus.DELIVERED || existingOrder.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new InvalidOrderStateException("Cannot patch an order in " + existingOrder.getOrderStatus() + " state");
        }

        if (request.getCustomerName() != null && !request.getCustomerName().isBlank()) {
            existingOrder.setCustomerName(request.getCustomerName().trim());
        }
        if (request.getCustomerPhone() != null && !request.getCustomerPhone().isBlank()) {
            existingOrder.setCustomerPhone(request.getCustomerPhone().trim());
        }
        if (request.getDeliveryAddress() != null && !request.getDeliveryAddress().isBlank()) {
            existingOrder.setDeliveryAddress(request.getDeliveryAddress().trim());
        }
        if (request.getFoodItem() != null && !request.getFoodItem().isBlank()) {
            existingOrder.setFoodItem(request.getFoodItem().trim());
        }
        if (request.getQuantity() != null) {
            existingOrder.setQuantity(request.getQuantity());
        }
        if (request.getPrice() != null) {
            existingOrder.setPrice(request.getPrice());
        }
        if (request.getSpecialInstructions() != null) {
            existingOrder.setSpecialInstructions(request.getSpecialInstructions().trim());
        }

        existingOrder.setUpdatedAt(LocalDateTime.now());
        orderStorage.put(orderId, existingOrder);
        return existingOrder;
    }

    @Override
    public void deleteOrder(Long orderId) {
        Order existingOrder = getOrderById(orderId);
        if (existingOrder.getOrderStatus() != OrderStatus.CANCELLED && existingOrder.getOrderStatus() != OrderStatus.DELIVERED) {
            throw new InvalidOrderStateException("Active orders cannot be deleted. Cancel the order first.");
        }
        orderStorage.remove(orderId);
    }

    @Override
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) throws OrderProcessingException {
        Order order = getOrderById(orderId);

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new OrderProcessingException("Cannot transition state for a CANCELLED order.");
        }
        if (order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new OrderProcessingException("Cannot change status of an already DELIVERED order.");
        }

        order.setOrderStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        orderStorage.put(orderId, order);
        return order;
    }

    @Override
    public Order cancelOrder(Long orderId) throws OrderProcessingException {
        Order order = getOrderById(orderId);

        if (order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new InvalidOrderStateException("Delivered orders cannot be cancelled.");
        }
        if (order.getOrderStatus() == OrderStatus.OUT_FOR_DELIVERY) {
            throw new OrderProcessingException("Order is already out for delivery. Driver contact required to cancel.");
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        orderStorage.put(orderId, order);
        return order;
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderStorage.values().stream()
                .filter(o -> o.getOrderStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> getOrdersByCustomerPhone(String customerPhone) {
        return orderStorage.values().stream()
                .filter(o -> o.getCustomerPhone().equals(customerPhone))
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> searchOrdersByFood(String query) {
        String lowerQuery = query.toLowerCase();
        return orderStorage.values().stream()
                .filter(o -> o.getFoodItem().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }
}