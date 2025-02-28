package tech.buildrun.ecommerce.controller.dto;

public record OrderItemDto(Integer quantity,
                           Long productId) {
}
