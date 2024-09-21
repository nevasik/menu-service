package ru.javaops.cloudjava.menuservice.storage.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IngredientCollection {
    private String title;
    private int amountCalories;

    public IngredientCollection() {
    }

    public IngredientCollection(String title, int amountCalories) {
        this.title = title;
        this.amountCalories = amountCalories;
    }
}
