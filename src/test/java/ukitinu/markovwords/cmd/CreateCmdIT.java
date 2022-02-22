package ukitinu.markovwords.cmd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ukitinu.markovwords.AlphabetUtils;
import ukitinu.markovwords.lib.FsUtils;
import ukitinu.markovwords.models.Dict;
import ukitinu.markovwords.repo.FileRepo;
import ukitinu.markovwords.repo.Repo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static ukitinu.markovwords.AlphabetUtils.WORD_END;

final class CreateCmdIT {
    private final String basePath = "./src/test/resources/dict_dir";
    private final Repo repo = FileRepo.create(basePath);
    private final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errStream = new ByteArrayOutputStream();

    private final CreateCmd cmd = new CreateCmd();

    @BeforeEach
    void setUp() {
        cmd.init(repo, new PrintStream(outStream), new PrintStream(errStream));
    }

    @Test
    void call() throws IOException {
        cmd.name = "new-dict";
        cmd.desc = "description";
        cmd.alphabet = "abc";
        try {
            assertFalse(repo.exists(cmd.name));
            assertEquals(0, cmd.call());
            assertEquals("New dictionary created: " + cmd.name + System.lineSeparator(), outStream.toString());
            assertTrue(repo.exists(cmd.name));

            Dict dict = assertDoesNotThrow(() -> repo.get(cmd.name));
            assertEquals(cmd.name, dict.name());
            assertEquals(cmd.desc, dict.desc());
            assertEquals(AlphabetUtils.convertToSet(cmd.alphabet + WORD_END), dict.alphabet());
        } finally {
            FsUtils.rmDir(Path.of(basePath, cmd.name));
        }
    }

    @Test
    void call_noDesc() throws IOException {
        cmd.name = "new-dict";
        cmd.alphabet = "abc";
        try {
            assertFalse(repo.exists(cmd.name));
            assertEquals(0, cmd.call());
            assertEquals("New dictionary created: " + cmd.name + System.lineSeparator(), outStream.toString());
            assertTrue(repo.exists(cmd.name));

            Dict dict = assertDoesNotThrow(() -> repo.get(cmd.name));
            assertEquals(cmd.name, dict.name());
            assertTrue(dict.desc().isEmpty());
            assertEquals(AlphabetUtils.convertToSet(cmd.alphabet + WORD_END), dict.alphabet());
        } finally {
            FsUtils.rmDir(Path.of(basePath, cmd.name));
        }
    }

    @Test
    void call_alreadyExists() {
        cmd.name = "dict-name";
        cmd.alphabet = "abc";
        assertEquals(1, cmd.call());
        assertEquals("there is already a dictionary named " + cmd.name + System.lineSeparator(), errStream.toString());
    }

    @Test
    void call_invalidName() {
        cmd.name = "";
        assertEquals(1, cmd.call());
        assertEquals("dict name must not be empty" + System.lineSeparator(), errStream.toString());
    }

    @Test
    void call_invalidDesc() {
        cmd.name = "my-name";
        cmd.desc = "\n";
        assertEquals(1, cmd.call());
        assertEquals("dict desc must consist of english letters, digits, whitespace and punctuation only" + System.lineSeparator(),
                errStream.toString());
    }

    @Test
    void call_invalidAlphabet() {
        cmd.name = "my-name";
        cmd.alphabet = "abc" + WORD_END;
        assertEquals(1, cmd.call());
        assertEquals("invalid alphabet:" + System.lineSeparator()
                + "invalid unicode at position 3 with hex value 5f" + System.lineSeparator(), errStream.toString());
    }
}