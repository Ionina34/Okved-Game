package ru.example.exeption.phone;

import lombok.Getter;

/**
 * Номер не соответствует формату российского мобильного номера
 * (неверная длина, не начинается на 9 после +7/8, содержит мусор и т.д.)
 */
@Getter
public class InvalidRussianMobileNumberException extends PhoneNumberException {

    private final String inputValue;
    private final String details;

    public InvalidRussianMobileNumberException(String input, String message, String details) {
        super(message);
        this.inputValue = input;
        this.details = details;
    }
}
