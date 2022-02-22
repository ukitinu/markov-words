package ukitinu.markovwords.cmd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ukitinu.markovwords.repo.FileRepo;
import ukitinu.markovwords.repo.Repo;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static ukitinu.markovwords.AlphabetUtils.WORD_END;

final class WriteCmdIT {
    private final String basePath = "./src/test/resources/dict_dir";
    private final Repo repo = FileRepo.create(basePath);
    private final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errStream = new ByteArrayOutputStream();

    private final WriteCmd cmd = new WriteCmd();

    @BeforeEach
    void setUp() {
        cmd.init(repo, new PrintStream(outStream), new PrintStream(errStream));
    }

    @Test
    void call() {
        cmd.name = "dict-complete";
        assertEquals(0, cmd.call());
        assertEquals(1, outStream.toString().split(System.lineSeparator()).length);
        assertFalse(outStream.toString().contains(String.valueOf(WORD_END)));
        assertFalse(outStream.toString().isBlank());
    }

    @Test
    void call_num() {
        cmd.name = "dict-complete";
        cmd.num = 5;
        assertEquals(0, cmd.call());
        String[] words = outStream.toString().split(System.lineSeparator());
        assertEquals(cmd.num, words.length);
        for (String word : words) {
            assertFalse(word.contains(String.valueOf(WORD_END)));
            assertFalse(word.isBlank());
        }
    }

    @Test
    void call_notExists() {
        cmd.name = "i-do-no-exist";
        assertEquals(1, cmd.call());
        assertEquals("dict not found: " + cmd.name + System.lineSeparator(), errStream.toString());
    }

    @Test
    void call_empty() {
        cmd.name = "dict-name";
        assertEquals(1, cmd.call());
        assertEquals("No grams in the dictionary" + System.lineSeparator(), errStream.toString());
    }

    @Test
    void call_wordEnd() {
        cmd.name = "dict-test";
        assertEquals(1, cmd.call());
        assertEquals("Missing WORD_END gram" + System.lineSeparator(), errStream.toString());
    }

    @Test
    void call_missing() {
        cmd.name = "dict-complete";
        cmd.depth = 3;
        assertEquals(1, cmd.call());
        assertEquals("Missing 3-grams" + System.lineSeparator(), errStream.toString());
    }
}