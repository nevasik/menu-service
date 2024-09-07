package ru.javaops.cloudjava.menuservice.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderMenuRequest {
    @NotEmpty(message = "Список названий не должен быть пустым.")
    private Set<String> menuNames;
}
