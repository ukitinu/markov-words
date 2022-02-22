package ukitinu.markovwords.cmd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ukitinu.markovwords.repo.FileRepo;
import ukitinu.markovwords.repo.Repo;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class ListCmdIT {
    private final String basePath = "./src/test/resources/dict_dir";
    private final Repo repo = FileRepo.create(basePath);
    private final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errStream = new ByteArrayOutputStream();

    private final ListCmd cmd = new ListCmd();

    @BeforeEach
    void setUp() {
        cmd.redirect(repo, new PrintStream(outStream), new PrintStream(errStream));
    }

    @Test
    void call() {
        assertEquals(0, cmd.call());
        assertEquals("bad-dict-dir" + System.lineSeparator()
                        + "dict-complete" + System.lineSeparator()
                        + "dict-name" + System.lineSeparator()
                        + "dict-test" + System.lineSeparator(),
                outStream.toString());
    }

    @Test
    void call_filter() {
        cmd.name = "e";
        assertEquals(0, cmd.call());
        assertEquals("dict-complete" + System.lineSeparator()
                        + "dict-name" + System.lineSeparator()
                        + "dict-test" + System.lineSeparator(),
                outStream.toString());
    }

    @Test
    void call_filterCaseInsensitive() {
        cmd.name = "A";
        assertEquals(0, cmd.call());
        assertEquals("bad-dict-dir" + System.lineSeparator()
                        + "dict-name" + System.lineSeparator(),
                outStream.toString());
    }

    @Test
    void call_deleted() {
        cmd.listDeleted = true;
        assertEquals(0, cmd.call());
        assertEquals(".del-dict" + System.lineSeparator()
                + ".restore-test" + System.lineSeparator(), outStream.toString());
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
                outStream.toString());
    }

    @Test
    void call_notFound() {
        cmd.name = "zzzzzzz";
        cmd.listAll = true;
        assertEquals(1, cmd.call());
        assertEquals("No results found" + System.lineSeparator(), errStream.toString());
    }
}