package ru.javaops.cloudjava.menuservice.storage.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IngredientCollection {
    private List<Ingredient> ingredients;
}
