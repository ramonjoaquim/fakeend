package br.com.fakeend.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

class EndpointDTOTest {

    @Test
    void testConstructorAndGetters() {
        String expectedName = "Example";
        String expectedPath = "/example";
        Integer expectedTimeout = 10;

        EndpointDTO dto = new EndpointDTO(expectedName, expectedPath, expectedTimeout);

        String actualName = dto.getName();
        String actualPath = dto.getPath();
        Integer actualTimeout = dto.getTimeout();

        Assertions.assertEquals(expectedName, actualName);
        Assertions.assertEquals(expectedPath, actualPath);
        Assertions.assertEquals(expectedTimeout, actualTimeout);
    }

    @Test
    void testValidation() {
        EndpointDTO dto = new EndpointDTO("", "", null);

        boolean isValid = validate(dto);

        Assertions.assertFalse(isValid);
    }

    private boolean validate(EndpointDTO dto) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<EndpointDTO>> violations = validator.validate(dto);
        return violations.isEmpty();
    }
}
