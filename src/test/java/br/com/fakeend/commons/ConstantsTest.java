package br.com.fakeend.commons;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ConstantsTest {

    @Test
    void testIDConstant() {
        String expected = "id";
        String actual = Constants.ID;
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testIDPathDefaultConstant() {
        Integer expected = -1;
        Integer actual = Constants.ID_PATH_DEFAULT;
        Assertions.assertEquals(expected, actual);
    }
}