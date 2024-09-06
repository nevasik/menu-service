package ru.javaops.cloudjava.menuservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.javaops.cloudjava.menuservice.dto.validation.NullOrNotBlank;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateMenuRequest {
    @NullOrNotBlank(message = "Название не должно быть пустым.")
    private String name;
    @NullOrNotBlank(message = "Описание не должно быть пустым.")
    private String description;
    @Positive(message = "Цена должна быть > 0.")
    private BigDecimal price;
    @Positive(message = "Время приготовления должно быть > 0.")
    private Long timeToCook;
    @NullOrNotBlank(message = "Ссылка на фото не должна быть пустой.")
    private String imageUrl;
}

