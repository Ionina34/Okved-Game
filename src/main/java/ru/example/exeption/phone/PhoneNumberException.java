package ru.example.exeption.phone;

/**
 * Базовое исключение для проблем с нормализацией номера телефона
 */
public class PhoneNumberException extends IllegalArgumentException {

    public PhoneNumberException(String message) {
        super(message);
    }
}
