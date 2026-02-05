package com.example.nautix.order.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import com.example.nautix.cart.model.CartItem;
import com.example.nautix.cart.repository.CartRepository;
import com.example.nautix.order.dto.OrderItemDTO;
import com.example.nautix.order.dto.OrderResponseDTO;
import com.example.nautix.order.dto.OrderStatusUpdateDTO;
import com.example.nautix.order.mapper.OrderMapper;
import com.example.nautix.order.model.Order;
import com.example.nautix.order.model.OrderItem;
import com.example.nautix.order.model.OrderStatus;
import com.example.nautix.order.repository.OrderItemRepository;
import com.example.nautix.order.repository.OrderRepository;
import com.example.nautix.productItem.model.ProductItem;
import com.example.nautix.productItem.repository.ProductItemRepository;
import com.example.nautix.user.model.User;
import com.example.nautix.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductItemRepository productItemRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    

    public OrderServiceImpl(OrderRepository orderRepository,OrderItemRepository orderItemRepo,
     CartRepository cartRepository,ProductItemRepository productItemRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository  = orderItemRepo;
        this.cartRepository=cartRepository;
        this.productItemRepository=productItemRepository;
        this.userRepository=userRepository;
    }

    @Override
    public List<OrderResponseDTO> getAll() {
        return orderRepository.findAll().stream()
                .map(OrderMapper::toDto)
                .toList();
    }

    

    @Override
    public OrderResponseDTO updateStatus(Long id, OrderStatusUpdateDTO updateDTO) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        order.setStatus(updateDTO.getStatus());
        return OrderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public List<OrderResponseDTO> getAllForUser(long userId){
    return orderRepository.findByUserId(userId).stream()
            .map(OrderMapper::toDto)
            .toList();

    }
  public OrderResponseDTO placeOrder(Long userId, List<OrderItemDTO> itemDTOs) {
    try {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> orderItems = itemDTOs.stream().map(dto -> {
            ProductItem productItem = productItemRepository.findById(dto.getProductItemId())
                .orElseThrow(() -> new RuntimeException("Product item not found: " + dto.getProductItemId()));

            OrderItem item = new OrderItem();
            item.setItem(productItem);
            item.setQuantity(dto.getQuantity());
            item.setPriceAtOrderTime(dto.getPriceAtOrderTime());
            item.setOrder(order);
            return item;
        }).collect(Collectors.toList());

        order.setItems(orderItems);

        double total = orderItems.stream()
            .mapToDouble(item -> item.getQuantity() * item.getPriceAtOrderTime())
            .sum();
        order.setTotalPrice(total);

        Order saved = orderRepository.save(order);

   cartRepository.findByUser(user).ifPresent(cart -> {
    List<Long> orderedItemIds = itemDTOs.stream()
        .map(OrderItemDTO::getProductItemId)
        .toList();

    List<CartItem> selectedItems = cart.getItems().stream()
        .filter(item -> orderedItemIds.contains(item.getItem().getId()))
        .toList();

    cart.getItems().removeAll(selectedItems); // or cart.clear(selectedItems) if defined
    cartRepository.save(cart);
});



        return OrderMapper.toDto(saved);

    } catch (RuntimeException e) {
        System.err.println("Error placing order: " + e.getMessage());
        throw e; // rethrow or use custom exception + handler
    }
}

     @Override
    public Page<OrderResponseDTO> getAll(Pageable pageable) {
        return orderRepository
            .findAll(pageable)
            .map(OrderMapper::toDto);
    }

    @Override
    public Page<OrderResponseDTO> getByStatus(OrderStatus status, Pageable pageable) {
        return orderRepository
            .findByStatus(status, pageable)
            .map(OrderMapper::toDto);
    }

    @Override
    public List<OrderItemDTO> getItemsForOrder(Long orderId) {
        // via direct repo:
        return orderItemRepository
            .findByOrderId(orderId)
            .stream()
            .map(OrderMapper::toItemDto)
            .collect(Collectors.toList());
    }

    @Override
    public OrderResponseDTO getByOrderId(Long orderId) {
        return orderRepository.findById(orderId)
            .map(OrderMapper::toDto)
            .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));
    }


}
