package ukitinu.markovwords.cmd;

import org.junit.jupiter.api.Test;
import ukitinu.markovwords.repo.FileRepo;
import ukitinu.markovwords.repo.Repo;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InfoCmdTest {
    private final String basePath = "./src/test/resources/dict_dir";
    private final Repo repo = FileRepo.create(basePath);
    private final ByteArrayOutputStream testStream = new ByteArrayOutputStream();
    private final InfoCmd infoCmd = new InfoCmd(repo, new PrintStream(testStream));

    @Test
    void call() {
        infoCmd.name = "dict-name";
        infoCmd.call();
        assertEquals("dict-name" + System.lineSeparator()
                        + "' - . 0 1 2 _ a b c" + System.lineSeparator(),
                testStream.toString());
    }

    @Test
    void call_verboseWithoutGrams() {
        infoCmd.name = "dict-name";
        infoCmd.verbose = true;
        infoCmd.call();
        assertEquals("dict-name" + System.lineSeparator()
                        + "' - . 0 1 2 _ a b c" + System.lineSeparator()
                        + "1-grams: " + System.lineSeparator()
                        + "2-grams: " + System.lineSeparator()
                        + "3-grams: " + System.lineSeparator(),
                testStream.toString());
    }

    @Test
    void call_verbose() {
        infoCmd.name = "dict-test";
        infoCmd.verbose = true;
        infoCmd.call();
        assertEquals("dict-test" + System.lineSeparator()
                        + "_ a b c d" + System.lineSeparator()
                        + "1-grams: a b" + System.lineSeparator()
                        + "2-grams: ba" + System.lineSeparator()
                        + "3-grams: " + System.lineSeparator(),
                testStream.toString());
    }
}