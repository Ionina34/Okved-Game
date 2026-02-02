package ru.example.matching;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.example.exeption.okved.InvalidMatchingInputException;
import ru.example.model.MatchResult;
import ru.example.model.Okved;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OkvedMatcherTest {

    private List<Okved> testOkveds;

    @BeforeEach
    void setUp() {
        Okved o1 = new Okved();
        o1.setCode("01.11.12");
        o1.setName("Выращивание ячменя");

        Okved o2 = new Okved();
        o2.setCode("47.11");
        o2.setName("Розничная торговля");

        Okved o3 = new Okved();
        o3.setCode("62.01");
        o3.setName("Разработка ПО");

        Okved o4 = new Okved();
        o4.setCode("Раздел A");  // без цифр → игнорируется

        testOkveds = new ArrayList<>();
        testOkveds.add(o1);
        testOkveds.add(o2);
        testOkveds.add(o3);
        testOkveds.add(o4);
    }

    @Test
    void findBestMatch_longestSuffixMatch() {
        MatchResult result = OkvedMatcher.findBestMatch("3456011112", testOkveds);
        assertEquals(6, result.getMatchLength());
        assertEquals("01.11.12", result.getOkved().getCode());
    }

    @Test
    void findBestMatch_multiplePossible_shouldPickLongest() {
        Okved shortMatch = new Okved();
        shortMatch.setName("Выращивание кактуса");
        shortMatch.setCode("11.12");
        testOkveds.add(shortMatch);

        MatchResult result = OkvedMatcher.findBestMatch("3456011112", testOkveds);
        assertEquals(6, result.getMatchLength()); // должен взять 01.11.12, а не 11.12
    }

    @Test
    void findBestMatch_noMatch_shouldReturnRandomWithLength0() {
        MatchResult result = OkvedMatcher.findBestMatch("9999999999", testOkveds);
        assertEquals(0, result.getMatchLength());
        assertNotNull(result.getOkved());
        assertEquals("Совпадений не найдено - показываем случайный ОКВЭД", result.getMessage());
    }

    @Test
    void findBestMatch_invalidPhoneDigits_shouldThrow() {
        assertThrows(InvalidMatchingInputException.class,
                () -> OkvedMatcher.findBestMatch("abc1234567", testOkveds));

        assertThrows(InvalidMatchingInputException.class,
                () -> OkvedMatcher.findBestMatch("123456789", testOkveds)); // 9 цифр

        assertThrows(InvalidMatchingInputException.class,
                () -> OkvedMatcher.findBestMatch(null, testOkveds));
    }

    @Test
    void findBestMatch_emptyOkvedList_shouldThrow() {
        assertThrows(InvalidMatchingInputException.class,
                () -> OkvedMatcher.findBestMatch("9001234567", List.of()));
    }
}
