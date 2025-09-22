package com.empuje.eventservice.model;

import com.empuje.eventservice.validation.FutureDateTime;
import com.empuje.eventservice.validation.FutureDateTimeValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EventValidationTest {

    private static Validator validator;
    
    @Mock
    private FutureDateTimeValidator futureDateTimeValidator;

    @BeforeAll
    static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void whenNameIsBlank_thenValidationFails() {
        Event event = Event.builder()
                .name("")
                .description("Descripción de prueba")
                .eventDatetime(Instant.now().plus(1, ChronoUnit.DAYS))
                .build();

        Set<ConstraintViolation<Event>> violations = validator.validate(event);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("El nombre del evento es obligatorio")));
    }

    @Test
    void whenNameExceedsMaxLength_thenValidationFails() {
        String longName = "a".repeat(201);
        Event event = Event.builder()
                .name(longName)
                .description("Descripción de prueba")
                .eventDatetime(Instant.now().plus(1, ChronoUnit.DAYS))
                .build();

        Set<ConstraintViolation<Event>> violations = validator.validate(event);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("El nombre del evento no puede exceder los 200 caracteres")));
    }

    @Test
    void whenDescriptionExceedsMaxLength_thenValidationFails() {
        String longDescription = "a".repeat(2001);
        Event event = Event.builder()
                .name("Evento de prueba")
                .description(longDescription)
                .eventDatetime(Instant.now().plus(1, ChronoUnit.DAYS))
                .build();

        Set<ConstraintViolation<Event>> violations = validator.validate(event);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("La descripción no puede exceder los 2000 caracteres")));
    }

    @Test
    void whenEventDateTimeIsNull_thenValidationFails() {
        Event event = Event.builder()
                .name("Evento de prueba")
                .description("Descripción de prueba")
                .eventDatetime(null)
                .build();

        Set<ConstraintViolation<Event>> violations = validator.validate(event);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("La fecha y hora del evento son obligatorias")));
    }

    @Test
    void whenEventIsInThePast_thenValidationFails() {
        Event event = Event.builder()
                .name("Evento de prueba")
                .description("Descripción de prueba")
                .eventDatetime(Instant.now().minus(1, ChronoUnit.DAYS))
                .build();

        Set<ConstraintViolation<Event>> violations = validator.validate(event);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("La fecha y hora del evento debe ser futura")));
    }

    @Test
    void whenAllFieldsAreValid_thenNoValidationErrors() {
        Event event = Event.builder()
                .name("Evento de prueba")
                .description("Descripción de prueba")
                .eventDatetime(Instant.now().plus(1, ChronoUnit.DAYS))
                .build();

        Set<ConstraintViolation<Event>> violations = validator.validate(event);
        assertTrue(violations.isEmpty());
    }
}
