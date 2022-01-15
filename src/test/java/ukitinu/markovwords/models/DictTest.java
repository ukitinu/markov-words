package ukitinu.markovwords.models;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static ukitinu.markovwords.AlphabetUtils.WORD_END;

class DictTest {

    @Test
    void hasWordEnd() {
        Dict dict = new Dict("test", Set.of());
        assertTrue(dict.alphabet().contains(WORD_END));
    }

}