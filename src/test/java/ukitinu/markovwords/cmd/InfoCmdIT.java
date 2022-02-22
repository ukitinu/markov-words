package ukitinu.markovwords.cmd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ukitinu.markovwords.repo.FileRepo;
import ukitinu.markovwords.repo.Repo;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class InfoCmdIT {
    private final String basePath = "./src/test/resources/dict_dir";
    private final Repo repo = FileRepo.create(basePath);
    private final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errStream = new ByteArrayOutputStream();

    private final InfoCmd cmd = new InfoCmd();

    @BeforeEach
    void setUp() {
        cmd.redirect(repo, new PrintStream(outStream), new PrintStream(errStream));
    }

    @Test
    void call_noDesc() {
        cmd.name = "dict-name";
        assertEquals(0, cmd.call());
        assertEquals("dict-name" + System.lineSeparator(), outStream.toString());
    }

    @Test
    void call() {
        cmd.name = "dict-complete";
        assertEquals(0, cmd.call());
        assertEquals("dict-complete" + System.lineSeparator()
                        + "A very good description" + System.lineSeparator(),
                outStream.toString());
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
                outStream.toString());
    }

    @Test
    void call_verbose() {
        cmd.name = "dict-test";
        cmd.verbose = true;
        assertEquals(0, cmd.call());
        assertEquals("dict-test" + System.lineSeparator()
                        + "description:test" + System.lineSeparator()
                        + "_ a b c d" + System.lineSeparator()
                        + "1-grams: a b" + System.lineSeparator()
                        + "2-grams: ba" + System.lineSeparator()
                        + "3-grams: " + System.lineSeparator(),
                outStream.toString());
    }

    @Test
    void call_deleted() {
        cmd.name = "del-dict";
        assertEquals(1, cmd.call());
        assertEquals("Dict has been deleted: " + cmd.name + System.lineSeparator()
                        + "Use ." + cmd.name + " to refer to it" + System.lineSeparator(),
                errStream.toString());
    }

    @Test
    void call_deletedWithDot() {
        cmd.name = ".del-dict";
        assertEquals(0, cmd.call());
        assertEquals("del-dict" + System.lineSeparator()
                        + "DELETED!" + System.lineSeparator(),
                outStream.toString());
    }

    @Test
    void call_notFound() {
        cmd.name = "i-do-not-exist";
        assertEquals(1, cmd.call());
        assertEquals("Dict not found: " + cmd.name + System.lineSeparator(), errStream.toString());
    }
}