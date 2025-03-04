package tech.buildrun.ecommerce.controller.dto;

import tech.buildrun.ecommerce.entities.OrderEntity;
import tech.buildrun.ecommerce.entities.OrderItemEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

public record OrderResponseDto(Long orderId,
                               BigDecimal total,
                               LocalDateTime orderDate,
                               UUID userId,
                               List<OrderItemResponseDto> items) {

    public static OrderResponseDto fromEntity(OrderEntity entity) {
        return new OrderResponseDto(
                entity.getOrderId(),
                entity.getTotal(),
                entity.getOrderDate(),
                entity.getUser().getUserId(),
                getItems(entity.getItems())
        );
    }

    private static List<OrderItemResponseDto> getItems(List<OrderItemEntity> items) {
        return items.stream()
                .map(OrderItemResponseDto::fromEntity)
                .toList();
    }
}
