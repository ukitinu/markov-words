package ukitinu.markovwords;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ukitinu.markovwords.models.Dict;
import ukitinu.markovwords.models.Gram;
import ukitinu.markovwords.readers.StringReader;
import ukitinu.markovwords.repo.Repo;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TextManagerIT {

    private final Dict dict = new Dict("test", AlphabetUtils.getAsciiLetters());
    private final Map<String, Gram> gramMap = new HashMap<>();
    private final String longerText = """
            "the quick brown fox jumps over the lazy dog" is an English-language pangram, a sentence that contains
             all of the letters of the English alphabet.
             Owing to its brevity and coherence, it has become widely known.
             The phrase is commonly used for touch-typing practice, testing typewriters and computer keyboards,
             displaying examples of fonts, and other applications involving text where the use of all letters
             in the alphabet is desired.
             THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG.""";

    private final Repo repo = Mockito.mock(Repo.class);
    private TextManager textManager;

    @BeforeEach
    void setUp() {
        Mockito.when(repo.getGramMap(dict)).thenReturn(gramMap);
        textManager = new TextManager(new StringReader(), new IngesterImpl(), repo);
    }

    @Test
    void ingest() {
        assertDoesNotThrow(() -> textManager.processText(longerText, dict, 1));
        assertEquals(dict.alphabet().size(), gramMap.size());
        assertDoesNotThrow(() -> textManager.processText(longerText, dict, 2));
        assertTrue(gramMap.size() > dict.alphabet().size());
        assertDoesNotThrow(() -> textManager.processText(longerText, dict, 3));
    }

}