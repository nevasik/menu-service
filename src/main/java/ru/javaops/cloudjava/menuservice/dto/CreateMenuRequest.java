package ru.javaops.cloudjava.menuservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import ru.javaops.cloudjava.menuservice.storage.model.Category;
import ru.javaops.cloudjava.menuservice.storage.model.IngredientCollection;

import java.math.BigDecimal;

@Data
@Builder
public class CreateMenuRequest {
    @NotBlank(message = "Название не должно быть пустым.")
    private String name;
    @NotBlank(message = "Описание не должно быть пустым.")
    private String description;
    @NotNull(message = "Цена не должна быть null.")
    @Positive(message = "Цена должна быть > 0.")
    private BigDecimal price;
    @NotNull(message = "Категория не должна быть null.")
    private Category category;
    @Positive(message = "Время приготовления должно быть > 0.")
    private long timeToCook;
    @Positive(message = "Вес должен быть > 0.")
    private double weight;
    @NotBlank(message = "Ссылка на фото не должна быть пустой.")
    private String imageUrl;
    @NotNull(message = "Ингредиенты не должны быть null.")
    private IngredientCollection ingredientCollection;
}
