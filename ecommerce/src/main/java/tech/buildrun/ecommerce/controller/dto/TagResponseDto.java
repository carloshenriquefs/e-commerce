package tech.buildrun.ecommerce.controller.dto;

import tech.buildrun.ecommerce.entities.TagEntity;

public record TagResponseDto(Long tagId,
                             String name) {

    public static TagResponseDto fromEntity(TagEntity entity) {
        return new TagResponseDto(
                entity.getTagId(),
                entity.getName()
        );
    }
}
