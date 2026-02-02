package ru.example.normalizer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.example.exeption.phone.EmptyPhoneInputException;
import ru.example.exeption.phone.InvalidRussianMobileNumberException;

import static org.junit.jupiter.api.Assertions.*;

public class PhoneNormalizerTest {

    @ParameterizedTest
    @CsvSource({
            "9001234567,               +79001234567",
            "89001234567,              +79001234567",
            "+79001234567,             +79001234567",
            "+7 (900) 123-45-67,       +79001234567",
            "7 900 123 45 67,          +79001234567",
            "8 900 123 45 67,          +79001234567",
            "   9001234567   ,         +79001234567"
    })
    void normalize_validInputs_shouldReturnCorrectFormat(String input, String expected) {
        assertEquals(expected, PhoneNormalizer.normalize(input));
    }

    @Test
    void normalize_emptyOrNull_shouldThrowEmptyException() {
        assertThrows(EmptyPhoneInputException.class, () -> PhoneNormalizer.normalize(null));
        assertThrows(EmptyPhoneInputException.class, () -> PhoneNormalizer.normalize("   "));
        assertThrows(EmptyPhoneInputException.class, () -> PhoneNormalizer.normalize(""));
    }

    @ParameterizedTest
    @CsvSource({
            "1234567890",           // не начинается на 9
            "12345678901",          // неверная длина
            "0123456789",           // начинается на 0
            "790012345678",         // лишняя цифра
            "+1 234 5678901"        // иностранный код
    })
    void normalize_invalidRussianMobile_shouldThrowInvalidException(String input) {
        InvalidRussianMobileNumberException ex = assertThrows(
                InvalidRussianMobileNumberException.class,
                () -> PhoneNormalizer.normalize(input)
        );
        assertNotNull(ex.getInputValue());
        assertTrue(ex.getMessage().contains("российски") || ex.getMessage().contains("длину"));
    }

    @Test
    void normalize_11digitsNotStartingWith78_shouldThrow() {
        assertThrows(InvalidRussianMobileNumberException.class,
                () -> PhoneNormalizer.normalize("91234567890"));
    }
}
