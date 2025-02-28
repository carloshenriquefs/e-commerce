package tech.buildrun.ecommerce.controller.dto;

import java.util.UUID;
import java.util.List;

public record CreateOrderDto(UUID userId,
                             List<OrderItemDto> items) {
}
