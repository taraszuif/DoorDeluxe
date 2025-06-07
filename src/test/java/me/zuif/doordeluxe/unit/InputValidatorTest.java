package me.zuif.doordeluxe.unit;

import me.zuif.doordeluxe.validator.InputValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InputValidatorTest {
    @Test
    void testValidatePassword_ToolShort() {
        assertFalse(InputValidator.validatePassword("1234567"));
    }

    @Test
    void testValidatePassword_TooLong() {
        assertFalse(InputValidator.validatePassword("a".repeat(257)));
    }

    @Test
    void testValidatePassword_ExactEight() {
        assertTrue(InputValidator.validatePassword("password"));
    }

    @Test
    void testValidatePassword_MaxLength() {
        assertTrue(InputValidator.validatePassword("a".repeat(255)));
    }

    @Test
    void testValidatePassword_MidLength() {
        assertTrue(InputValidator.validatePassword("a".repeat(128)));
    }

    @Test
    void testValidateEmail_InvalidSymbol() {
        assertFalse(InputValidator.validateEmail("test#test.com"));
    }

    @Test
    void testValidateEmail_MissingLocalPart() {
        assertFalse(InputValidator.validateEmail("@test.com"));
    }

    @Test
    void testValidateEmail_MultipleAtSymbols() {
        assertFalse(InputValidator.validateEmail("test@test@test.com"));
    }

    @Test
    void testValidateEmail_MissingDomain() {
        assertFalse(InputValidator.validateEmail("test@"));
    }

    @Test
    void testValidateEmail_MissingTLD() {
        assertFalse(InputValidator.validateEmail("test@test"));
    }

    @Test
    void testValidateEmail_DoubleDot() {
        assertFalse(InputValidator.validateEmail("test@test..com"));
    }

    @Test
    void testValidateEmail_DoubleTLD() {
        assertTrue(InputValidator.validateEmail("test@test.com.com"));
    }

    @Test
    void testValidateEmail_ValidEmail() {
        assertTrue(InputValidator.validateEmail("test@test.com"));
    }

    @Test
    void testValidateEmail_ValidEmailLong() {
        assertTrue(InputValidator.validateEmail("testtestst@tesffft.com"));
    }
}
