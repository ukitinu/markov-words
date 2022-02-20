package ukitinu.markovwords;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ukitinu.markovwords.models.Dict;
import ukitinu.markovwords.models.Gram;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class IngesterIT {

    private final Dict dict = new Dict("test", AlphabetUtils.getAsciiLetters());
    private Map<String, Gram> gramMap;
    private final String longerText = """
            "the quick brown fox jumps over the lazy dog" is an English-language pangram, a sentence that contains
             all of the letters of the English alphabet.
             Owing to its brevity and coherence, it has become widely known.
             The phrase is commonly used for touch-typing practice, testing typewriters and computer keyboards,
             displaying examples of fonts, and other applications involving text where the use of all letters
             in the alphabet is desired.
             THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG.""";

    private final Ingester ingester = new Ingester();

    @BeforeEach
    void setUp() {
        gramMap = new HashMap<>();
    }

    @Test
    void ingest() {
        assertDoesNotThrow(() -> ingester.ingest(longerText, gramMap, dict));
        assertFalse(gramMap.isEmpty());
        assertTrue(gramMap.containsKey("_th"));
        assertTrue(gramMap.containsKey("OG_"));
        assertTrue(gramMap.containsKey("z"));
        assertTrue(gramMap.containsKey("th"));
        assertTrue(gramMap.containsKey("qui"));
        assertTrue(gramMap.containsKey("ck"));
    }

    @Test
    void ingest_longText() {
        assertDoesNotThrow(() -> ingester.ingest(longerText, gramMap, dict, 1));
        assertEquals(dict.alphabet().size(), gramMap.size());
        assertDoesNotThrow(() -> ingester.ingest(longerText, gramMap, dict, 2));
        assertTrue(gramMap.size() > dict.alphabet().size());
        assertDoesNotThrow(() -> ingester.ingest(longerText, gramMap, dict, 3));
    }

}