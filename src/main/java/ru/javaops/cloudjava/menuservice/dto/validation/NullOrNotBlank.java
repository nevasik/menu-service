package ru.javaops.cloudjava.menuservice.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = NullOrNotBlankValidator.class)
public @interface NullOrNotBlank {
    String message() default "Поле должно быть null или не пустым.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
