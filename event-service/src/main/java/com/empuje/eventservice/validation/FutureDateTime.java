package com.empuje.eventservice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FutureDateTimeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface FutureDateTime {
    String message() default "La fecha y hora del evento debe ser futura";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
