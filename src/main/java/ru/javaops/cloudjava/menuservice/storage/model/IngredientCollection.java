package ru.javaops.cloudjava.menuservice.storage.model;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IngredientCollection {
    private List<Ingredient> ingredients;
}
