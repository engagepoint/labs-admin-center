package com.engagepoint.university.admincentre.web;

import java.text.MessageFormat;
import static org.junit.Assert.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author artem.lysenko
 */
public class ValidatorAddTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidatorAddTest.class.getName());

        public ValidatorAddTest() {
    }

    /**
     * Test of isDouble method, of class ValidatorAdd.
     */
    @Test
public void testIsDouble() {
        System.out.println("isDouble");
        ValidatorAddTest instance = new ValidatorAddTest();
        String testInput = "1.234567890";
        boolean result = instance.isDouble(testInput);
        assertTrue(result);

        testInput = "text";
        result = instance.isDouble(testInput);
        assertFalse(result);
    }

    private boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
        } catch (NumberFormatException e) {
            LOGGER.error(MessageFormat.format("Can not parse value {0} to Double", str), e);
            return false;
        }
        return true;
    }
}