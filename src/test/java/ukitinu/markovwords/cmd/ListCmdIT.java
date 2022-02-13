package ukitinu.markovwords.cmd;

import org.junit.jupiter.api.Test;

import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class ListCmdIT extends CmdITHelper {
    private final ListCmd cmd = new ListCmd(repo, new PrintStream(testStream));

    @Test
    void call() {
        assertEquals(0, cmd.call());
        assertEquals("bad-dict-dir" + System.lineSeparator()
                        + "dict-complete" + System.lineSeparator()
                        + "dict-name" + System.lineSeparator()
                        + "dict-test" + System.lineSeparator(),
                testStream.toString());
    }

    @Test
    void call_filter() {
        cmd.name = "e";
        assertEquals(0, cmd.call());
        assertEquals("dict-complete" + System.lineSeparator()
                        + "dict-name" + System.lineSeparator()
                        + "dict-test" + System.lineSeparator(),
                testStream.toString());
    }

    @Test
    void call_filterCaseInsensitive() {
        cmd.name = "A";
        assertEquals(0, cmd.call());
        assertEquals("bad-dict-dir" + System.lineSeparator()
                        + "dict-name" + System.lineSeparator(),
                testStream.toString());
    }

    @Test
    void call_deleted() {
        cmd.listDeleted = true;
        assertEquals(0, cmd.call());
        assertEquals(".del-dict" + System.lineSeparator()
                + ".restore-test" + System.lineSeparator(), testStream.toString());
    }

    @Test
    void call_all() {
        cmd.name = "e";
        cmd.listAll = true;
        assertEquals(0, cmd.call());
        assertEquals("dict-complete" + System.lineSeparator()
                        + "dict-name" + System.lineSeparator()
                        + "dict-test" + System.lineSeparator()
                        + ".del-dict" + System.lineSeparator()
                        + ".restore-test" + System.lineSeparator(),
                testStream.toString());
    }

    @Test
    void call_notFound() {
        cmd.name = "zzzzzzz";
        cmd.listAll = true;
        assertEquals(1, cmd.call());
        assertEquals("No results found" + System.lineSeparator(), testStream.toString());
    }
}