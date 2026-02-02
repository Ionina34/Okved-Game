package ru.example.exeption.phone;

/**
 * Входные данные пустые / null / только пробелы
 */
public class EmptyPhoneInputException extends PhoneNumberException{

    public EmptyPhoneInputException(String message){
        super(message);
    }
}
