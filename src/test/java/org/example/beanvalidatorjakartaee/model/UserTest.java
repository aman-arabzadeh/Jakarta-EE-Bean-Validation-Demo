package org.example.beanvalidatorjakartaee.model;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void validateValidUser() {
        var user = createValidUser();
        var violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @CsvSource({
            "'', name, 'Name must not be blank'",
            "invalid-email, email, 'Email should be valid'",
            "15, age, 'Age must be at least 18'",
            "-1, userId, 'ID must be a positive number'"
    })
    void shouldInvalidateUserFields(String invalidValue, String property, String expectedMessage) {
        var user = createValidUser();
        switch (property) {
            case "name" -> user.setName(invalidValue);
            case "email" -> user.setEmail(invalidValue);
            case "age" -> user.setAge(Integer.parseInt(invalidValue));
            case "userId" -> user.setUserId(Integer.parseInt(invalidValue));
            default -> fail("Unknown property: " + property);
        }

        var violations = validator.validate(user);
        assertEquals(1, violations.size(), "Expected one violation for " + property);
        var v = violations.iterator().next();
        assertEquals(property, v.getPropertyPath().toString());
        assertEquals(expectedMessage, v.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "abc, 2, 'Phone number must be 10 digits;Phone number must contain only digits'",
            "12345, 1, 'Phone number must be 10 digits'"
    })
    void shouldInvalidatePhoneNumber(String invalidPhone, int expectedCount, String combinedMessages) {
        var user = createValidUser();
        user.setPhoneNumber(invalidPhone);

        var violations = validator.validate(user);
        assertEquals(expectedCount, violations.size(), "Expected " + expectedCount + " violation(s) for phoneNumber");

        var expectedMessages = List.of(combinedMessages.split(";"));
        var actualMessages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .toList();

        for (var msg : expectedMessages) {
            assertTrue(actualMessages.contains(msg), "Expected message: " + msg);
        }
    }

    @Test
    void shouldInvalidateNestedAddress() {
        var badAddress = new Address(
                "",      // street blank
                "City",  // city ok
                "",      // state ok
                "12",    // zip too short
                ""       // country blank
        );
        var user = createValidUser();
        user.setAddress(badAddress);

        var violations = validator.validate(user);
        assertEquals(3, violations.size(), "Expected three address violations");

        var messages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .toList();
        assertTrue(messages.contains("Street cannot be empty"));
        assertTrue(messages.contains("Invalid zip code format (e.g., 12345 or 12345-6789)"));
        assertTrue(messages.contains("Country cannot be empty"));
    }

    private User createValidUser() {
        return new User(
                "John", "john@example.com", 25, 1,
                "1234567890",
                new Address("123 Main St", "Springfield", "IL", "62704", "USA")
        );
    }
}
