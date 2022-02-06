package ukitinu.markovwords;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}