package ukitinu.markovwords;

import org.junit.jupiter.api.Test;
import ukitinu.markovwords.models.Dict;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static ukitinu.markovwords.AlphabetUtils.WORD_END;

class TextManagerTest {

    @Test
    void cleanText() {
        var dict = new Dict("test", Set.of('A', 'b', 'c'));
        var tm = new TextManager(null, null);
        String str = "the quick brown fox jumps over the lazy dog. THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG";

        String cleaned = assertDoesNotThrow(() -> tm.cleanText(str, dict));
        assertTrue(cleaned.startsWith(String.valueOf(WORD_END)));
        assertFalse(cleaned.endsWith(String.valueOf(WORD_END)));
        assertEquals("_c_b_A", cleaned);
    }

    @Test
    void cleanText_longer() {
        var dict = new Dict("test", Set.of('a', 'b', 'c'));
        var tm = new TextManager(null, null);
        String str = """
                "The quick brown fox jumps over the lazy dog" is an English-language pangram, a sentence that contains
                 all of the letters of the English alphabet.
                 Owing to its brevity and coherence, it has become widely known.
                 The phrase is commonly used for touch-typing practice, testing typewriters and computer keyboards,
                 displaying examples of fonts, and other applications involving text where the use of all letters
                 in the alphabet is desired.""";

        String cleaned = assertDoesNotThrow(() -> tm.cleanText(str, dict));
        assertTrue(cleaned.startsWith(String.valueOf(WORD_END)));
        assertFalse(cleaned.endsWith(String.valueOf(WORD_END)));
        assertFalse(cleaned.contains(String.valueOf(WORD_END) + String.valueOf(WORD_END)));
        assertFalse(cleaned.contains("z"));
    }
}