package ru.example.exeption.okved;

/**
 * Базовое исключение для всех проблем, связанных со справочником ОКВЭД
 */
public abstract class OkvedException extends IllegalArgumentException {

    public OkvedException(String s) {
        super(s);
    }

    public OkvedException(String message, Throwable cause) {
        super(message, cause);
    }
}
