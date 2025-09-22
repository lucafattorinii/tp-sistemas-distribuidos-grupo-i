package com.empuje.eventservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class FutureDateTimeValidator implements ConstraintValidator<FutureDateTime, Instant> {
    
    @Override
    public void initialize(FutureDateTime constraintAnnotation) {
        // No initialization needed
    }
    
    @Override
    public boolean isValid(Instant value, ConstraintValidatorContext context) {
        if (value == null) {
            return false; // Let @NotNull handle null values
        }
        
        // Add a small buffer to account for the time between validation and persistence
        Instant now = Instant.now().minus(1, ChronoUnit.SECONDS);
        return value.isAfter(now);
    }
}
