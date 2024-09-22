package ru.javaops.cloudjava.menuservice.storage.model;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;
import ru.javaops.cloudjava.menuservice.util.DateUtil;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "menu_items")
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(name = "time_to_cook", nullable = false)
    private long timeToCook;

    @Column(name = "weight", nullable = false)
    private double weight;

    @Column(name = "image_url", nullable = false)
    private String imageURL;

    @Column(name = "create_at", nullable = false)
    @CreationTimestamp
    @DateTimeFormat(pattern = DateUtil.DATE_FORMAT)
    private Date createAt;

    @Column(name = "update_at", nullable = false)
    @UpdateTimestamp
    @DateTimeFormat(pattern = DateUtil.DATE_FORMAT)
    private Date updateAt;

    @Type(JsonBinaryType.class)
    @Column(name = "ingredient_collection", columnDefinition = "jsonb")
    private IngredientCollection ingredientCollection;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuItem menuItem = (MenuItem) o;
        return id == menuItem.id;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
