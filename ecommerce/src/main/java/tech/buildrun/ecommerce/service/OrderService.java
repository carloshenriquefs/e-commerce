package tech.buildrun.ecommerce.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import tech.buildrun.ecommerce.controller.dto.CreateOrderDto;
import tech.buildrun.ecommerce.controller.dto.OrderItemDto;
import tech.buildrun.ecommerce.controller.dto.OrderSummaryDto;
import tech.buildrun.ecommerce.entities.OrderEntity;
import tech.buildrun.ecommerce.entities.OrderItemEntity;
import tech.buildrun.ecommerce.entities.OrderItemId;
import tech.buildrun.ecommerce.entities.ProductEntity;
import tech.buildrun.ecommerce.entities.UserEntity;
import tech.buildrun.ecommerce.exception.CreateOrderException;
import tech.buildrun.ecommerce.repository.OrderRepository;
import tech.buildrun.ecommerce.repository.ProductRepository;
import tech.buildrun.ecommerce.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public OrderEntity createOrder(CreateOrderDto dto) {

        var order = new OrderEntity();

        var user = validateUser(dto);

        var orderItems = validateOrderItems(order, dto);

        var total = calculateOrderTotal(orderItems);

        order.setOrderDate(LocalDateTime.now());
        order.setUser(user);
        order.setItems(orderItems);
        order.setTotal(total);

        return orderRepository.save(order);
    }

    private UserEntity validateUser(CreateOrderDto dto) {
        return userRepository.findById(dto.userId())
                .orElseThrow(() -> new CreateOrderException("user not found"));
    }

    private List<OrderItemEntity> validateOrderItems(OrderEntity order,
                                                     CreateOrderDto dto) {

        if (dto.items().isEmpty()) {
            throw new CreateOrderException("order items is empty");
        }

        return dto.items()
                .stream()
                .map(ordemItemDto -> getOrderItem(order, ordemItemDto))
                .toList();
    }

    private OrderItemEntity getOrderItem(OrderEntity order,
                                         OrderItemDto ordemItemDto) {

        var orderItemEntity = new OrderItemEntity();
        var id = new OrderItemId();
        var product = getProduct(ordemItemDto.productId());

        id.setOrder(order);
        id.setProduct(product);

        orderItemEntity.setId(id);
        orderItemEntity.setQuantity(ordemItemDto.quantity());
        orderItemEntity.setSalePrice(product.getPrice());

        return orderItemEntity;
    }

    private ProductEntity getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new CreateOrderException("product not found"));
    }

    private BigDecimal calculateOrderTotal(List<OrderItemEntity> items) {

        return items.stream()
                .map(i -> i.getSalePrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    public Page<OrderSummaryDto> findAll(Integer page, Integer pageSize) {
        return orderRepository.findAll(PageRequest.of(page, pageSize))
                .map(entity -> {
                    return new OrderSummaryDto(
                            entity.getOrderId(),
                            entity.getOrderDate(),
                            entity.getUser().getUserId(),
                            entity.getTotal()
                    );
                });
    }

    public Optional<OrderEntity> findById(Long orderId) {
        return orderRepository.findById(orderId);
    }
}
