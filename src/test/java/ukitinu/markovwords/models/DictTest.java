package ukitinu.markovwords.models;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static ukitinu.markovwords.AlphabetUtils.WORD_END;

class DictTest {

    @Test
    void hasWordEnd() {
        Dict dict1 = new Dict("test", Set.of());
        assertTrue(dict1.alphabet().contains(WORD_END));

        Dict dict2 = new Dict("test", "desc", Set.of());
        assertTrue(dict2.alphabet().contains(WORD_END));
    }

}