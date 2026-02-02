package ru.example.exeption.okved;

/**
 * Исключение для пустого справочника
 */
public class OkvedEmptyException extends OkvedException {

    public OkvedEmptyException(String message) {
        super(message);
    }
}
