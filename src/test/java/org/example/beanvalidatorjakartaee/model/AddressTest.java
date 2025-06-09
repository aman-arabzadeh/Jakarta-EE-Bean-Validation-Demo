package org.example.beanvalidatorjakartaee.model;


import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class AddressTest {
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void validateValidAddress() {
        var addr = new Address("123 Main St", "Springfield", "IL", "62704", "USA");
        var violations = validator.validate(addr);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @CsvSource({
            "'', street, 'Street cannot be empty'",
            "ThisStreetNameIsWayTooLongToBeValidBecauseItExceeds, street, 'Street must be at most 100 characters'",
            "'', city, 'City cannot be empty'",
            "ThisCityNameIsWayTooLongToBeValidBecauseItExceedsFiftyCharacters_ABCDEFGHIJ, city, 'City must be at most 50 characters'",
            "123, zipCode, 'Invalid zip code format (e.g., 12345 or 12345-6789)'",
            "'', country, 'Country cannot be empty'"
    })
    void shouldInvalidateAddressFields(String invalidValue, String property, String expectedMessage) {
        var addr = new Address("123 Main St", "Springfield", "IL", "62704", "USA");
        switch (property) {
            case "street" -> addr.setStreet(invalidValue.repeat(4));
            case "city" -> addr.setCity(invalidValue);
            case "zipCode" -> addr.setZipCode(invalidValue);
            case "country" -> addr.setCountry(invalidValue);
            default -> fail("Unknown property: " + property);
        }

        var violations = validator.validate(addr);
        assertEquals(1, violations.size(), "Expected one violation for " + property);
        var v = violations.iterator().next();
        assertEquals(property, v.getPropertyPath().toString());
        assertEquals(expectedMessage, v.getMessage());
    }
}
