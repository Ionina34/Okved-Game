package ru.example.exeption.okved;

/**
 * Входные данные для поиска некорректны
 * (неверный формат номера, пустой список ОКВЭД и т.п.)
 */
public class InvalidMatchingInputException extends OkvedException{

    public InvalidMatchingInputException(String message){
        super(message);
    }
}
