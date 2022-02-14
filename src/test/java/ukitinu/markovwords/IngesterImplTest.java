package ukitinu.markovwords;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ukitinu.markovwords.models.Dict;
import ukitinu.markovwords.models.Gram;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static ukitinu.markovwords.AlphabetUtils.WORD_END;

class IngesterImplTest {

    private final Ingester ingester = new IngesterImpl();
    private final Dict dict = new Dict("test", Set.of('h', 'e', 'l', 'o'));
    private final String text = WORD_END + "hello";
    private Map<String, Gram> gramMap;

    @BeforeEach
    void setUp() {
        gramMap = new HashMap<>();
    }

    @Test
    void ingest_1gram() {
        assertDoesNotThrow(() -> ingester.ingest(text, gramMap, dict, 1));
        assertEquals(5, gramMap.size());

        assertEquals(1, gramMap.get(String.valueOf(WORD_END)).get('h'));
        assertEquals(1, gramMap.get("h").get('e'));
        assertEquals(1, gramMap.get("e").get('l'));
        assertEquals(1, gramMap.get("l").get('l'));
        assertEquals(1, gramMap.get("l").get('o'));
        assertEquals(1, gramMap.get("o").get(WORD_END));
    }

    @Test
    void ingest_2gram_andStartingValues() {
        var he = new Gram("he", dict, Map.of('l', 5));
        var lo = new Gram("lo", dict, Map.of('l', 2));

        gramMap.put("he", he);
        gramMap.put("lo", lo);

        assertDoesNotThrow(() -> ingester.ingest(text, gramMap, dict, 2));
        assertEquals(6, gramMap.size());

        assertEquals(1, gramMap.get(WORD_END + "h").get('e'));
        assertEquals(6, gramMap.get("he").get('l')); // +1
        assertEquals(1, gramMap.get("el").get('l'));
        assertEquals(1, gramMap.get("ll").get('o'));
        assertEquals(1, gramMap.get("lo").get(WORD_END));
        assertEquals(2, gramMap.get("lo").get('l')); // unchanged
        assertEquals(1, gramMap.get("o" + WORD_END).get(WORD_END));
    }

    @Test
    void ingest_3gram() {
        assertDoesNotThrow(() -> ingester.ingest(text, gramMap, dict, 3));
        assertEquals(5, gramMap.size());

        assertEquals(1, gramMap.get(WORD_END + "he").get('l'));
        assertEquals(1, gramMap.get("hel").get('l'));
        assertEquals(1, gramMap.get("ell").get('o'));
        assertEquals(1, gramMap.get("llo").get(WORD_END));
        assertEquals(1, gramMap.get("lo" + WORD_END).get(WORD_END));
    }

    @Test
    void ingest_noOp() {
        assertDoesNotThrow(() -> ingester.ingest(text, gramMap, dict, text.length() + 3)); // 2 WORD_ENDs + 1
        assertTrue(gramMap.isEmpty());
    }

    @Test
    void ingest_illegalParameter() {
        var dict = new Dict("test", Set.of('a'));
        assertThrows(IllegalArgumentException.class, () -> ingester.ingest(null, Map.of(), dict, 1));
        assertThrows(IllegalArgumentException.class, () -> ingester.ingest("text", null, dict, 1));
        assertThrows(IllegalArgumentException.class, () -> ingester.ingest("text", Map.of(), null, 1));
        assertThrows(IllegalArgumentException.class, () -> ingester.ingest("text", Map.of(), dict, 0));
        assertThrows(IllegalArgumentException.class, () -> ingester.ingest("text", Map.of(), dict, -1));
    }
}