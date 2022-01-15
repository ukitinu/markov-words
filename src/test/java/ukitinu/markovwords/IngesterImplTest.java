package ukitinu.markovwords;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ukitinu.markovwords.models.Dict;
import ukitinu.markovwords.models.Gram;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class IngesterImplTest {

    private final Ingester ingester = new IngesterImpl();
    private final Dict dict = new Dict("test", Set.of('h', 'e', 'l', 'o'));
    private final String text = "_hello";
    private Map<String, Gram> gramMap;

    @BeforeEach
    void setUp() {
        gramMap = new HashMap<>();
    }

    @Test
    void ingest_1gram() {
        assertDoesNotThrow(() -> ingester.ingest(text, gramMap, dict, 1));
        assertEquals(5, gramMap.size());

        assertEquals(1, gramMap.get("_").get('h'));
        assertEquals(1, gramMap.get("h").get('e'));
        assertEquals(1, gramMap.get("e").get('l'));
        assertEquals(1, gramMap.get("l").get('l'));
        assertEquals(1, gramMap.get("l").get('o'));
        assertEquals(1, gramMap.get("o").get('_'));
    }

    @Test
    void ingest_2gram_andStartingValues() {
        var he = new Gram("he", dict, Map.of('l', 5));
        var lo = new Gram("lo", dict, Map.of('l', 2));

        gramMap.put("he", he);
        gramMap.put("lo", lo);

        assertDoesNotThrow(() -> ingester.ingest(text, gramMap, dict, 2));
        assertEquals(5, gramMap.size());

        assertEquals(1, gramMap.get("_h").get('e'));
        assertEquals(6, gramMap.get("he").get('l'));
        assertEquals(1, gramMap.get("el").get('l'));
        assertEquals(1, gramMap.get("ll").get('o'));
        assertEquals(1, gramMap.get("lo").get('_'));
        assertEquals(2, gramMap.get("lo").get('l'));
    }

    @Test
    void ingest_3gram() {
        assertDoesNotThrow(() -> ingester.ingest(text, gramMap, dict, 3));
        assertEquals(4, gramMap.size());

        assertEquals(1, gramMap.get("_he").get('l'));
        assertEquals(1, gramMap.get("hel").get('l'));
        assertEquals(1, gramMap.get("ell").get('o'));
        assertEquals(1, gramMap.get("llo").get('_'));
    }

    @Test
    void ingest_4gram() {
        assertDoesNotThrow(() -> ingester.ingest(text, gramMap, dict, 4));
        assertEquals(3, gramMap.size());

        assertEquals(1, gramMap.get("_hel").get('l'));
        assertEquals(1, gramMap.get("hell").get('o'));
        assertEquals(1, gramMap.get("ello").get('_'));
    }

    @Test
    void ingest_5gram() {
        assertDoesNotThrow(() -> ingester.ingest(text, gramMap, dict, 5));
        assertEquals(2, gramMap.size());

        assertEquals(1, gramMap.get("_hell").get('o'));
        assertEquals(1, gramMap.get("hello").get('_'));
    }

    @Test
    void ingest_6gram() {
        assertDoesNotThrow(() -> ingester.ingest(text, gramMap, dict, 6));
        assertEquals(1, gramMap.size());

        assertEquals(1, gramMap.get("_hello").get('_'));
    }

    @Test
    void ingest_7gram() {
        assertDoesNotThrow(() -> ingester.ingest(text, gramMap, dict, 7));
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