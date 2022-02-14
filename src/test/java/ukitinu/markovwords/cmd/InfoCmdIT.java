package ukitinu.markovwords.cmd;

import org.junit.jupiter.api.Test;

import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class InfoCmdIT extends CmdITHelper {
    private final InfoCmd cmd = new InfoCmd(repo, new PrintStream(testStream));

    @Test
    void call() {
        cmd.name = "dict-name";
        assertEquals(0, cmd.call());
        assertEquals("dict-name" + System.lineSeparator()
                        + "' - . 0 1 2 _ a b c" + System.lineSeparator(),
                testStream.toString());
    }

    @Test
    void call_verboseWithoutGrams() {
        cmd.name = "dict-name";
        cmd.verbose = true;
        assertEquals(0, cmd.call());
        assertEquals("dict-name" + System.lineSeparator()
                        + "' - . 0 1 2 _ a b c" + System.lineSeparator()
                        + "1-grams: " + System.lineSeparator()
                        + "2-grams: " + System.lineSeparator()
                        + "3-grams: " + System.lineSeparator(),
                testStream.toString());
    }

    @Test
    void call_verbose() {
        cmd.name = "dict-test";
        cmd.verbose = true;
        assertEquals(0, cmd.call());
        assertEquals("dict-test" + System.lineSeparator()
                        + "_ a b c d" + System.lineSeparator()
                        + "1-grams: a b" + System.lineSeparator()
                        + "2-grams: ba" + System.lineSeparator()
                        + "3-grams: " + System.lineSeparator(),
                testStream.toString());
    }

    @Test
    void call_deleted() {
        cmd.name = "del-dict";
        assertEquals(1, cmd.call());
        assertEquals("Dict has been deleted: " + cmd.name + System.lineSeparator()
                        + "Use ." + cmd.name + " to refer to it" + System.lineSeparator(),
                testStream.toString());
    }

    @Test
    void call_deletedWithDot() {
        cmd.name = ".del-dict";
        assertEquals(0, cmd.call());
        assertEquals("del-dict" + System.lineSeparator()
                        + "_ a b c d e f g" + System.lineSeparator(),
                testStream.toString());
    }

    @Test
    void call_notFound() {
        cmd.name = "i-do-not-exist";
        assertEquals(1, cmd.call());
        assertEquals("Dict not found: " + cmd.name + System.lineSeparator(), testStream.toString());
    }
}