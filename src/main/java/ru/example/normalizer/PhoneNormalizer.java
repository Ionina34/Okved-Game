package ru.example.normalizer;

import ru.example.exeption.phone.EmptyPhoneInputException;
import ru.example.exeption.phone.InvalidRussianMobileNumberException;
import ru.example.exeption.phone.PhoneNumberException;

/**
 * Нормализатор для российских мобильных номеров к виду +79ХХХХХХХХХ
 */
public class PhoneNormalizer {

    /**
     * Нормализует российский мобильный номер к виду +79ХХХХХХХХХ
     *
     * @param input любой формат номера
     * @return нормализованный номер +79ХХХХХХХХХ
     * @throws IllegalArgumentException если номер нельзя распознать как российский мобильный
     */
    public static String normalize(String input) throws PhoneNumberException {
        if (input == null || input.trim().isEmpty()) {
            throw new EmptyPhoneInputException("Номер пустой или null");
        }

        // Оставляем только цифры
        String digits = input.replaceAll("[^0-9]", "");

        // Возможные варианты длины после очистки
        switch (digits.length()) {
            case 10:
                // Предполагаем, что это 9XX XXX XX XX → 79...
                digits = "7" + digits;
                break;
            case 11:
                // 8XXX... или 7XXX...
                char first = digits.charAt(0);
                if (first == '8' || first == '7') {
                    digits = "7" + digits.substring(1);
                } else {
                    throw new InvalidRussianMobileNumberException(
                            input,
                            "Номер не является российским - 11-значый номер должен начинаться с 8 или 7",
                            "После нормализации: " + digits
                    );
                }
                break;
            default:
                throw new InvalidRussianMobileNumberException(
                        input,
                        "Номер имеет неверную длину после нормализации",
                        "После нормализации: " + digits
                );
        }

        // Финальная проверка — строго мобильный РФ
        if (!digits.startsWith("79")) {
            throw new InvalidRussianMobileNumberException(
                    input,
                    "Получен не российский мобильный номер",
                    "После нормализации: " + digits
            );
        }

        return "+" + digits;
    }
}
