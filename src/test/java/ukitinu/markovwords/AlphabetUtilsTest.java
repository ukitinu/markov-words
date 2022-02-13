package ukitinu.markovwords;

import org.junit.jupiter.api.Test;
import ukitinu.markovwords.models.Dict;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static ukitinu.markovwords.AlphabetUtils.WORD_END;

class AlphabetUtilsTest {

    @Test
    void convertToSet() {
        assertEquals(Set.of('a', 'b', 'c', '\u0002', '3'), AlphabetUtils.convertToSet("c3\u0002ab"));
    }

    @Test
    void getAsciiLettersUpper() {
        assertEquals(Set.of('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
                        'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'),
                AlphabetUtils.getAsciiLettersUpper());
    }

    @Test
    void getAsciiLettersLower() {
        assertEquals(Set.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'),
                AlphabetUtils.getAsciiLettersLower());
    }

    @Test
    void getAsciiDigits() {
        assertEquals(Set.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9'), AlphabetUtils.getAsciiDigits());
    }

    @Test
    void cleanText() {
        var dict = new Dict("test", Set.of('A', 'b', 'c'));
        String str = "the quick brown fox jumps over the lazy dog. THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG";

        String cleaned = assertDoesNotThrow(() -> AlphabetUtils.cleanText(str, dict));
        assertTrue(cleaned.startsWith(String.valueOf(WORD_END)));
        assertTrue(cleaned.endsWith(String.valueOf(WORD_END)));
        String expected = String.format("%sc%sb%sA%s", WORD_END, WORD_END, WORD_END, WORD_END);
        assertEquals(expected, cleaned);
    }

    @Test
    void cleanText_longer() {
        var dict = new Dict("test", Set.of('a', 'b', 'c'));
        String str = """
                "The quick brown fox jumps over the lazy dog" is an English-language pangram, a sentence that contains
                 all of the letters of the English alphabet.
                 Owing to its brevity and coherence, it has become widely known.
                 The phrase is commonly used for touch-typing practice, testing typewriters and computer keyboards,
                 displaying examples of fonts, and other applications involving text where the use of all letters
                 in the alphabet is desired.""";

        String cleaned = assertDoesNotThrow(() -> AlphabetUtils.cleanText(str, dict));
        assertTrue(cleaned.startsWith(String.valueOf(WORD_END)));
        assertTrue(cleaned.endsWith(String.valueOf(WORD_END)));
        assertFalse(cleaned.contains(String.valueOf(WORD_END) + String.valueOf(WORD_END)));
        assertFalse(cleaned.contains("z"));
    }
}