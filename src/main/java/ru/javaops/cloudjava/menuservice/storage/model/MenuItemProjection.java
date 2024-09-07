package ru.javaops.cloudjava.menuservice.storage.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemProjection {
    private String name;
    private BigDecimal price;
}
