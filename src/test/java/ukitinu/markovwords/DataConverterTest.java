package ukitinu.markovwords;

import org.junit.jupiter.api.Test;
import ukitinu.markovwords.models.Dict;
import ukitinu.markovwords.models.Gram;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ukitinu.markovwords.AlphabetUtils.WORD_END;
import static ukitinu.markovwords.DataConverter.GRAM_MAP_SEP;
import static ukitinu.markovwords.DataConverter.NAME_SEP;

class DataConverterTest {
    private final DataConverter dc = new DataConverter();

    @Test
    void serialiseDict() {
        Set<Character> alphabet = Set.of('a', '-', '\'', 'b', 'c', '0', '1', '2', '<', '\u001F', WORD_END);
        Dict dict = new Dict("dict-name", alphabet);
        String serial = assertDoesNotThrow(() -> dc.serialiseDict(dict));

        long sepCount = serial.chars().filter(ch -> ch == NAME_SEP).count();
        assertEquals(1, sepCount);

        String[] parts = serial.split(String.valueOf(NAME_SEP));

        assertEquals("dict-name", parts[0]);
        assertEquals(alphabet, AlphabetUtils.convertToSet(parts[1]));
    }

    @Test
    void serialiseGram() {
        Dict dict = new Dict("dict-name", Set.of('a', 'b', 'c', '1', '0', '\''));
        Map<Character, Integer> charMap = Map.of(
                'a', 10,
                'b', 0,
                'c', 2,
                '1', 1,
                '0', 1,
                '\'', 0
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
                new HashSet<>(Arrays.asList("a10", "b0", "c2", "11", "01", "'0")),
                new HashSet<>(Arrays.asList(parts[1].split(String.valueOf(GRAM_MAP_SEP))))
        );
    }

    @Test
    void deserialiseDict() {
    }

    @Test
    void deserialiseGram() {
    }
}