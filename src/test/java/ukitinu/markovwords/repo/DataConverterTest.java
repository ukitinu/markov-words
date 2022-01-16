package ukitinu.markovwords.repo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ukitinu.markovwords.AlphabetUtils;
import ukitinu.markovwords.models.Dict;
import ukitinu.markovwords.models.Gram;
import ukitinu.markovwords.repo.DataConverter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static ukitinu.markovwords.AlphabetUtils.WORD_END;
import static ukitinu.markovwords.repo.DataConverter.GRAM_MAP_SEP;
import static ukitinu.markovwords.repo.DataConverter.NAME_SEP;

class DataConverterTest {
    private final DataConverter dc = new DataConverter();

    @Test
    void serialiseDict() {
        Set<Character> alphabet = Set.of('a', '-', '\'', 'b', 'c', '0', '1', '2', '<', '_', '\u00e6', WORD_END);
        Dict dict = new Dict("dict-name", alphabet);
        String serial = assertDoesNotThrow(() -> dc.serialiseDict(dict));

        long sepCount = serial.chars().filter(ch -> ch == NAME_SEP).count();
        assertEquals(1, sepCount);

        String[] parts = serial.split(String.valueOf(NAME_SEP));

        assertEquals("dict-name", parts[0]);
        Assertions.assertEquals(alphabet, AlphabetUtils.convertToSet(parts[1]));
    }

    @Test
    void serialiseGram() {
        Dict dict = new Dict("dict-name", Set.of('a', 'b', 'c', '1', '0', '\'', '\u00e6'));
        Map<Character, Integer> charMap = Map.of(
                'a', 10,
                'b', 0,
                'c', 2,
                '1', 1,
                '0', 1,
                '\'', 0,
                '\u00e6', 7
        );
        Gram gram = new Gram("ab1", dict, charMap);
        String serial = assertDoesNotThrow(() -> dc.serialiseGram(gram));

        long sepCount = serial.chars().filter(ch -> ch == NAME_SEP).count();
        assertEquals(1, sepCount);

        String[] parts = serial.split(String.valueOf(NAME_SEP));

        assertEquals("ab1", parts[0]);

        long charMapSize = parts[1].chars().filter(ch -> ch == GRAM_MAP_SEP).count();
        assertEquals(charMap.size(), charMapSize);
        assertEquals(
                new HashSet<>(Arrays.asList("a10", "b0", "c2", "11", "01", "'0", "æ7")), //æ is u00e6
                new HashSet<>(Arrays.asList(parts[1].split(String.valueOf(GRAM_MAP_SEP))))
        );
    }

    @Test
    void deserialiseDict() {
        String name = "my-dict-name";
        String alphabet = "aopurag182,ooo.-1!'" + WORD_END;
        String serial = name + NAME_SEP + alphabet;
        Dict dict = assertDoesNotThrow(() -> dc.deserialiseDict(serial));
        assertEquals(name, dict.name());
        assertEquals(AlphabetUtils.convertToSet(alphabet), dict.alphabet());
    }

    @Test
    void deserialiseGram() {
        Dict dict = new Dict("placeholder", Set.of());
        String value = "gram-value";
        String charMap = "a2;02;110;\u00e61;" + WORD_END + "4;";
        String serial = value + NAME_SEP + charMap;
        Gram gram = assertDoesNotThrow(() -> dc.deserialiseGram(serial, dict));
        assertEquals(value, gram.getValue());
        assertFalse(gram.isEmpty());
        assertEquals(
                Map.of('a', 2, '0', 2, '1', 10, '\u00e6', 1, WORD_END, 4),
                gram.getCharMap()
        );
    }
}