package co.edu.uco.mihorario.domain.model.valueobject;

import co.edu.uco.mihorario.crosscutting.helpers.StringHelper;

public record EmployeeIdentification(String value) {

    public EmployeeIdentification {
        if (!StringHelper.stringNotNullOrEmpty(value).isEmpty() && !StringHelper.stringNotNullOrEmpty(value).isBlank()) {
            throw new IllegalArgumentException("Employee identification cannot be empty.");
        }

        value = value.trim();

        if (value.length() < 5 || value.length() > 15) {
            throw new IllegalArgumentException("Identification must be between 5 and 15 characters long.");
        }

        if (!value.matches("\\d+")) {
            throw new IllegalArgumentException("Identification must contain digits only.");
        }
    }
}