package ukitinu.markovwords.cmd;

import org.junit.jupiter.api.Test;
import ukitinu.markovwords.repo.FileRepo;
import ukitinu.markovwords.repo.Repo;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListCmdTest {
    private final String basePath = "./src/test/resources/dict_dir";
    private final Repo repo = FileRepo.create(basePath);
    private final ByteArrayOutputStream testStream = new ByteArrayOutputStream();
    private final ListCmd listCmd = new ListCmd(repo, new PrintStream(testStream));

    @Test
    void call() {
        listCmd.call();
        assertEquals("bad-dict-dir" + System.lineSeparator()
                        + "dict-name" + System.lineSeparator()
                        + "dict-test" + System.lineSeparator(),
                testStream.toString());
    }

    @Test
    void call_filter() {
        listCmd.name = "e";
        listCmd.call();
        assertEquals("dict-name" + System.lineSeparator()
                        + "dict-test" + System.lineSeparator(),
                testStream.toString());
    }

    @Test
    void call_filterCaseInsensitive() {
        listCmd.name = "A";
        listCmd.call();
        assertEquals("bad-dict-dir" + System.lineSeparator()
                        + "dict-name" + System.lineSeparator(),
                testStream.toString());
    }

    @Test
    void call_deleted() {
        listCmd.listDeleted = true;
        listCmd.call();
        assertEquals(".del-dict" + System.lineSeparator(), testStream.toString());
    }

    @Test
    void call_all() {
        listCmd.name = "e";
        listCmd.listAll = true;
        listCmd.call();
        assertEquals("dict-name" + System.lineSeparator()
                        + "dict-test" + System.lineSeparator()
                        + ".del-dict" + System.lineSeparator(),
                testStream.toString());
    }
}