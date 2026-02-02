package ru.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.PrimitiveIterator;

/**
 * Результат поиска - лучший ОКВЭД и длина совпадения
 */
@AllArgsConstructor
@Getter
public class MatchResult {

    /**
     * Лучший ОКВЭД
     */
    private final Okved okved;

    /**
     * Длина совпадения
     */
    private final int matchLength;

    /**
     * Опционально: для резервной стратегии
     */
    private final String message;

    public MatchResult(Okved okved, int matchLength) {
        this(okved, matchLength, null);
    }
}
