package com.example.nautix.order.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.nautix.order.dto.OrderItemDTO;
import com.example.nautix.order.dto.OrderResponseDTO;
import com.example.nautix.order.dto.OrderStatusUpdateDTO;
import com.example.nautix.order.model.OrderStatus;
import com.example.nautix.order.service.OrderService;

import jakarta.validation.Valid;

import java.util.List;


@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getByOrderId(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(@PathVariable Long id,
            @RequestBody OrderStatusUpdateDTO updateDTO) {
        return ResponseEntity.ok(orderService.updateStatus(id, updateDTO));
    }


    @PostMapping()
    public ResponseEntity<OrderResponseDTO> placeOrder(@RequestParam Long  userId,@Valid @RequestBody  List<OrderItemDTO> items) {
        OrderResponseDTO response  = orderService.placeOrder(userId, items);
        return ResponseEntity.ok(response) ;      
    }

     @GetMapping("/first")
    public ResponseEntity<Page<OrderResponseDTO>> getAll(@PageableDefault(size = 1)Pageable pageable) {
        return ResponseEntity.ok(orderService.getAll(pageable));
    }

    @GetMapping("/status")
    public ResponseEntity<Page<OrderResponseDTO>> getByStatus(
            @RequestParam OrderStatus status,
            @PageableDefault(size = 1)Pageable pageable) {
        return ResponseEntity.ok(orderService.getByStatus(status, pageable));
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<List<OrderItemDTO>> getItems(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getItemsForOrder(id));
    }

    
    //@GetMapping("/userId")
    //public ResponseEntity<List<OrderResponseDTO>> getOrdersForUser(@PathVariable long userId){
        //ResponseEntity.ok(orderService.getAllForUser(userId));

        //}
    }




