package com.foodorder.system.service;

import com.foodorder.system.exception.OrderProcessingException;
import com.foodorder.system.model.*;

import java.util.List;

public interface OrderService {
    Order createOrder(OrderCreateRequest request) throws OrderProcessingException;
    List<Order> getAllOrders();
    Order getOrderById(Long orderId);
    Order updateOrder(Long orderId, OrderUpdateRequest request);
    Order patchOrder(Long orderId, OrderPatchRequest request);
    void deleteOrder(Long orderId);
    Order updateOrderStatus(Long orderId, OrderStatus status) throws OrderProcessingException;
    Order cancelOrder(Long orderId) throws OrderProcessingException;
    List<Order> getOrdersByStatus(OrderStatus status);
    List<Order> getOrdersByCustomerPhone(String customerPhone);
    List<Order> searchOrdersByFood(String query);
}