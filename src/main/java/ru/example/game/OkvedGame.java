package ru.example.game;

import ru.example.downloader.OkvedCache;
import ru.example.exeption.ExitGameException;
import ru.example.exeption.okved.*;
import ru.example.exeption.phone.EmptyPhoneInputException;
import ru.example.exeption.phone.InvalidRussianMobileNumberException;
import ru.example.exeption.phone.PhoneNumberException;
import ru.example.matching.OkvedMatcher;
import ru.example.model.MatchResult;
import ru.example.model.Okved;
import ru.example.normalizer.PhoneNormalizer;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Основной класс игры «Найди свой ОКВЭД по номеру телефона»
 * Содержит всю игровую логику, ввод-вывод, обработку ошибок.
 * Метод main находится в отдельном классе.
 */
public class OkvedGame {

    private static final String OKVED_JSON_URL = "https://raw.githubusercontent.com/bergstar/testcase/refs/heads/master/okved.json";

    private final Scanner scanner;
    private final OkvedCache okvedCache;

    public OkvedGame() {
        this.scanner = new Scanner(System.in);
        this.okvedCache = OkvedCache.getInstance(OKVED_JSON_URL);
    }

    /**
     * Запускает игру (основная точка входа для логики)
     */
    public void start(){
        printWelcome();

        while (true){
            try{
                playOneRound();
            } catch (ExitGameException e) {
                System.out.println("\nСпасибо за игру! До новых встреч ✌️");
                return;
            } catch (EmptyPhoneInputException e) {
                System.out.println("Вы ничего не ввели. Попробуйте снова.");
                continue;
            } catch (InvalidRussianMobileNumberException e) {
                System.out.println("Ошибка в номере телефона:");
                System.out.println("  → " + e.getMessage());
                if (e.getInputValue() != null) {
                    System.out.println("  Вы ввели: " + e.getInputValue());
                }
                System.out.println("Попробуйте в формате: +79001234567, 89001234567, 9001234567");
                continue;
            } catch (InvalidMatchingInputException e) {
                System.out.println("Ошибка при поиске совпадения: " + e.getMessage());
                System.out.println("Это внутренняя проблема, попробуйте ещё раз.");
                continue;
            } catch (OkvedDownloadException e) {
                System.out.println("Не удалось скачать справочник ОКВЭД:");
                System.out.println("  URL: " + e.getUrl());
                System.out.println("  HTTP статус: " + e.getHttpStatus());
                System.out.println("  Ответ сервера (фрагмент): " + e.getResponsePreview());
                System.out.println("Возможно, проблема с интернетом или файл временно недоступен.");
                System.out.println("Игра не может продолжаться без справочника. Попробуйте позже.");
                return;
            } catch (OkvedParseException e) {
                System.out.println("Справочник ОКВЭД загружен, но не удалось его разобрать:");
                System.out.println("  → " + e.getMessage());
                System.out.println("  Фрагмент JSON: " + e.getJsonSnippet());
                System.out.println("Вероятно, структура файла изменилась. Игра временно недоступна.");
                return;
            } catch (OkvedEmptyException e) {
                System.out.println("Справочник ОКВЭД пустой после обработки:");
                System.out.println("  → " + e.getMessage());
                System.out.println("Без данных игра невозможна. Попробуйте позже.");
                return;
            } catch (IOException e) {
                // На случай, если где-то вылетело чистое IOException
                System.out.println("Сетевая ошибка: " + e.getMessage());
                System.out.println("Попробуйте проверить интернет-соединение.");
                return;
            } catch (Exception e) {
                // Последний рубеж — всё остальное
                System.out.println("Неожиданная ошибка в игре:");
                System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
                e.printStackTrace(System.err);
                System.out.println("Попробуем продолжить с новым номером...");
                continue;
            }

            // Предложение сыграть ещё
            System.out.print("\nЕщё раз? (да / нет / выход): ");
            String resp = scanner.nextLine().trim().toLowerCase();
            if (resp.startsWith("н") || resp.equals("нет") || resp.equals("выход")) {
                System.out.println("Хорошо, до свидания!");
                return;
            }
            System.out.println();
        }
    }

    private void playOneRound() throws IOException, PhoneNumberException, OkvedException{
        System.out.print("Введите мобильный номер (или 'выход'): ");
        String input = scanner.nextLine().trim();

        if (input.equalsIgnoreCase("выход") || input.equalsIgnoreCase("exit")) {
            throw new ExitGameException();
        }

        String normalized = PhoneNormalizer.normalize(input);
        System.out.println("Нормализованный номер: " + normalized);

        // Кэш сам решает — загружать или брать из памяти
        List<Okved> okveds = okvedCache.getOkveds();

        String phoneDigits = normalized.substring(2); // +7 → 10 цифр
        MatchResult result = OkvedMatcher.findBestMatch(phoneDigits, okveds);

        printResult(normalized, result);
    }

    private void printWelcome() {
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║         Добро пожаловать в игру            ║");
        System.out.println("║   «Найди свой ОКВЭД по номеру телефона»    ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.println("Введите мобильный номер — и узнайте,");
        System.out.println("какой вид деятельности вам «судьбой предначертан»!\n");
    }

    private void printResult(String normalized, MatchResult result) {
        System.out.println("╔════════════════════════════════════════════╗");

        if (result.getOkved() == null) {
            System.out.println("║ Результат не получен...                    ║");
        } else {
            Okved o = result.getOkved();
            String code = o.getCode() != null ? o.getCode() : "—";
            String name = o.getName() != null ? o.getName() : "—";

            if (result.getMatchLength() > 0) {
                System.out.printf("║ Номер:      %-32s ║%n", normalized);
                System.out.printf("║ ОКВЭД:      %s — %s ║%n", code, name);
                System.out.printf("║ Совпадение: %d цифр                         ║%n", result.getMatchLength());

                if (result.getMatchLength() >= 8) {
                    System.out.println("║ Это почти судьба! 🔥                        ║");
                } else if (result.getMatchLength() >= 5) {
                    System.out.println("║ Довольно близко... Может, стоит присмотреться? 😉 ║");
                } else {
                    System.out.println("║ Совпадение слабенькое, но всё равно интересно! ║");
                }
            } else {
                System.out.printf("║ Номер:      %-32s ║%n", normalized);
                System.out.println("║ Прямого совпадения нет...                  ║");
                System.out.println("║ Случайный ОКВЭД на удачу:                  ║");
                System.out.printf("║ %s — %s ║%n", code, name);
                System.out.println("║ Иногда Вселенная намекает именно так 🌌     ║");
            }
        }

        System.out.println("╚════════════════════════════════════════════╝");
    }
}

