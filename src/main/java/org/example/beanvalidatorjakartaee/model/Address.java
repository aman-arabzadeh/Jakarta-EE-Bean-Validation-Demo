package org.example.beanvalidatorjakartaee.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @NotBlank(message = "Street cannot be empty")
    @Size(max = 100, message = "Street must be at most 100 characters")
    private String street;

    @NotBlank(message = "City cannot be empty")
    @Size(max = 50, message = "City must be at most 50 characters")
    private String city;

    @Size(max = 50, message = "State must be at most 50 characters")
    private String state;

    @NotBlank(message = "Zip code cannot be empty")
    @Pattern(regexp = "\\d{5}(-\\d{4})?", message = "Invalid zip code format (e.g., 12345 or 12345-6789)")
    private String zipCode;

    @NotBlank(message = "Country cannot be empty")
    @Size(max = 50, message = "Country must be at most 50 characters")
    private String country;
}