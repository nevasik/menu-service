package ru.javaops.cloudjava.menuservice.storage.model;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Collection;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "menu_items", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
@Setter
@Getter
public class MenuItem {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "name")
    @NotNull
    private String name;

    @Column(name = "description")
    @NotNull
    private String description;

    @Column(name = "price")
    private double price;

    @Column(name = "category")
    private Category category;

    @Column(name = "time_to_cook")
    @NotNull
    private long timeToCook;

    @Column(name = "weight")
    @NotNull
    private double weight;

    @Column(name = "image_url")
    @NotNull
    private String imageURL;

    @Column(name = "ingredient_collection")
    @Type(JsonBinaryType.class)
    @NotNull
    private IngredientCollection[] ingredientCollection;

    @Column(name = "create_at")
    @CreationTimestamp
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    private Date createAt;

    @Column(name = "update_at", columnDefinition = "jsonb")
    @UpdateTimestamp
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    private Date updateAt;

    public MenuItem(){
    }

    public MenuItem(long id, String name, String description, double price, Category category, long timeToCook, double weight, String imageURL,
                    IngredientCollection[] ingredientCollection, Date createAt, Date updateAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.timeToCook = timeToCook;
        this.weight = weight;
        this.imageURL = imageURL;
        this.ingredientCollection = ingredientCollection;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuItem menuItem = (MenuItem) o;
        return Double.compare(price, menuItem.price) == 0 && Objects.equals(name, menuItem.name)
                && Objects.equals(category, menuItem.category) && Objects.equals(createAt, menuItem.createAt);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
