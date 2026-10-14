package com.foodorder.system.controller;

import com.foodorder.system.exception.OrderProcessingException;
import com.foodorder.system.model.*;
import com.foodorder.system.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 1. POST - Create new Order
    @PostMapping
    public ResponseEntity<ApiResponse<Order>> createOrder(@Valid @RequestBody OrderCreateRequest request) throws OrderProcessingException {
        Order created = orderService.createOrder(request);
        return new ResponseEntity<>(ApiResponse.success("Order created successfully", created), HttpStatus.CREATED);
    }

    // 2. GET - Retrieve all Orders
    @GetMapping
    public ResponseEntity<ApiResponse<List<Order>>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(ApiResponse.success("Retrieved all orders", orders));
    }

    // 3. GET - Retrieve Order by ID
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Order>> getOrderById(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(ApiResponse.success("Order retrieved successfully", order));
    }

    // 4. PUT - Complete Order Update
    @PutMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Order>> updateOrder(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderUpdateRequest request) {
        Order updated = orderService.updateOrder(orderId, request);
        return ResponseEntity.ok(ApiResponse.success("Order fully updated", updated));
    }

    // 5. PATCH - Partial Update
    @PatchMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Order>> patchOrder(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderPatchRequest request) {
        Order patched = orderService.patchOrder(orderId, request);
        return ResponseEntity.ok(ApiResponse.success("Order partially updated", patched));
    }

    // 6. DELETE - Remove Order
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok(ApiResponse.success("Order deleted successfully", null));
    }

    // 7. PATCH - Update Order Status
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<Order>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) throws OrderProcessingException {
        Order updated = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(ApiResponse.success("Order status updated to " + status, updated));
    }

    // 8. POST - Cancel Order
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<Order>> cancelOrder(@PathVariable Long orderId) throws OrderProcessingException {
        Order cancelled = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", cancelled));
    }

    // 9. GET - Filter by Status
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<Order>>> getOrdersByStatus(@PathVariable OrderStatus status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Orders retrieved by status " + status, orders));
    }

    // 10. GET - Filter by Customer Phone Number
    @GetMapping("/customer/{phone}")
    public ResponseEntity<ApiResponse<List<Order>>> getOrdersByCustomerPhone(@PathVariable String phone) {
        List<Order> orders = orderService.getOrdersByCustomerPhone(phone);
        return ResponseEntity.ok(ApiResponse.success("Customer orders retrieved", orders));
    }

    // 11. GET - Search food items
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Order>>> searchOrdersByFood(@RequestParam String query) {
        List<Order> orders = orderService.searchOrdersByFood(query);
        return ResponseEntity.ok(ApiResponse.success("Search results for: " + query, orders));
    }
}