package br.com.fakeend.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TimeoutDTOTest {

    @Test
    void testConstructorAndGetters() {
        Integer timeout = 10;
        TimeoutDTO dto = new TimeoutDTO(timeout);

        Assertions.assertEquals(timeout, dto.getTimeout());
    }

    @Test
    void testConstructor() {
        TimeoutDTO dto = new TimeoutDTO();

        Assertions.assertNull(dto.getTimeout());
    }
}
