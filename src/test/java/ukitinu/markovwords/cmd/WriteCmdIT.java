package ukitinu.markovwords.cmd;

import org.junit.jupiter.api.Test;

import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static ukitinu.markovwords.AlphabetUtils.WORD_END;

final class WriteCmdIT extends CmdITHelper {
    private final WriteCmd cmd = new WriteCmd(repo, new PrintStream(testStream));

    @Test
    void call() {
        cmd.name = "dict-complete";
        assertEquals(0, cmd.call());
        assertEquals(1, testStream.toString().split(System.lineSeparator()).length);
        assertFalse(testStream.toString().contains(String.valueOf(WORD_END)));
        assertFalse(testStream.toString().isBlank());
    }

    @Test
    void call_num() {
        cmd.name = "dict-complete";
        cmd.num = 5;
        assertEquals(0, cmd.call());
        String[] words = testStream.toString().split(System.lineSeparator());
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
        assertEquals("Dict not found: " + cmd.name + System.lineSeparator(), testStream.toString());
    }
}