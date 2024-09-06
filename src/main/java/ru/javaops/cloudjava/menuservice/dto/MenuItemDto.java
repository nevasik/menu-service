package ru.javaops.cloudjava.menuservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import ru.javaops.cloudjava.menuservice.storage.model.Category;
import ru.javaops.cloudjava.menuservice.storage.model.IngredientCollection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MenuItemDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Category category;
    private long timeToCook;
    private double weight;
    private String imageUrl;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
    private IngredientCollection ingredientCollection;
}
