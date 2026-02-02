package ru.example.matching;

import ru.example.exeption.okved.InvalidMatchingInputException;
import ru.example.exeption.okved.OkvedException;
import ru.example.model.MatchResult;
import ru.example.model.Okved;

import java.util.List;
import java.util.Random;

/**
 * Находит ОКВЭД с наибольшим совпадением по суффиксу телефонного номера
 */
public class OkvedMatcher {

    /**
     * Находит ОКВЭД с максимальным совпадением суффикса с номером телефона.
     *
     * @param phoneDigits строка из 10 цифр без +7
     * @param okveds      плоский список всех ОКВЭД
     * @return MatchResult с лучшим совпадением (или случайный при нулевом совпадении)
     */
    public static MatchResult findBestMatch(String phoneDigits, List<Okved> okveds) throws OkvedException {
        if (phoneDigits == null || phoneDigits.length() != 10 || !phoneDigits.matches("\\d{10}")) {
            throw new InvalidMatchingInputException("phoneDigits должен быть строкой из 10 цифр");
        }
        if (okveds == null || okveds.isEmpty()) {
            throw new InvalidMatchingInputException("Список ОКВЭД  пустой или null");
        }

        int maxLength = 0;
        Okved best = null;

        for (Okved item : okveds) {
            String current = cleanCode(item.getCode());
            if (current.isEmpty()) continue;

            int len = current.length();
            // уже не может быть лучше
            if (len <= maxLength) continue;

            if (phoneDigits.endsWith(current)) {
                maxLength = len;
                best = item;
                // почти полное совпадение — дальше бессмысленно
                if (len >= 8) break;
            }
        }

        if (maxLength > 0 && best != null) {
            return new MatchResult(best, maxLength);
        }

        // Резервная стратегия — случайный ОКВЭД
        Random random = new Random();
        Okved randomOkved = okveds.get(random.nextInt(okveds.size()));
        return new MatchResult(
                randomOkved,
                0,
                "Совпадений не найдено - показываем случайный ОКВЭД"
        );
    }

    /**
     * Удаляет все не-цифровые символы из кода ОКВЭД
     */
    private static String cleanCode(String code) {
        if (code == null) return "";
        return code.replaceAll("\\D", "");
    }
}
