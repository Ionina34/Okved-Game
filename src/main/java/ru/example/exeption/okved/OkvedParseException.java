package ru.example.exeption.okved;

import lombok.Getter;

/**
 * Проблемы с парсингом JSON (не соответствует модели, неожиданная структура, битый JSON)
 */
@Getter
public class OkvedParseException extends OkvedException {

    private final String jsonSnippet;

    public OkvedParseException(String message, Throwable cause) {
        this(message, cause, null);
    }

    public OkvedParseException(String message, Throwable cause, String jsonSnippet) {
        super(message, cause);
        this.jsonSnippet = jsonSnippet != null
                ? jsonSnippet.substring(0, Math.min(200, jsonSnippet.length()))
                : "(no snippet)";
    }
}
