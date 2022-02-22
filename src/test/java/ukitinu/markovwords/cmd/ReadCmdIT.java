package ukitinu.markovwords.cmd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ukitinu.markovwords.Ingester;
import ukitinu.markovwords.lib.FsUtils;
import ukitinu.markovwords.repo.FileRepo;
import ukitinu.markovwords.repo.Repo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static ukitinu.markovwords.AlphabetUtils.WORD_END;

final class ReadCmdIT {
    private final String basePath = "./src/test/resources/dict_dir";
    private final Repo repo = FileRepo.create(basePath);
    private final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errStream = new ByteArrayOutputStream();

    private final ReadCmd cmd = new ReadCmd();

    @BeforeEach
    void setUp() {
        cmd.init(repo, new PrintStream(outStream), new PrintStream(errStream));
        cmd.input = new ReadCmd.ReadInput();
    }

    @Test
    void call_text() throws IOException {
        cmd.name = "dict-test";
        cmd.input.text = "cd";
        try {
            FsUtils.cpDir(Path.of(basePath, cmd.name), Path.of(basePath, cmd.name + ".backup"));

            var gramMap = repo.getGramMap(cmd.name);
            assertFalse(gramMap.containsKey(cmd.input.text));
            assertFalse(gramMap.containsKey(String.valueOf(WORD_END)));

            assertEquals(0, cmd.call());
            assertEquals("Text read, dictionary " + cmd.name + " updated" + System.lineSeparator(), outStream.toString());

            gramMap = repo.getGramMap(cmd.name);
            assertTrue(gramMap.containsKey(cmd.input.text));
            assertTrue(gramMap.containsKey(String.valueOf(WORD_END)));
        } finally {
            FsUtils.rmDir(Path.of(basePath, cmd.name));
            FsUtils.cpDir(Path.of(basePath, cmd.name + ".backup"), Path.of(basePath, cmd.name));
            FsUtils.rmDir(Path.of(basePath, cmd.name + ".backup"));
        }
    }

    @Test
    void call_file() throws IOException {
        cmd.name = "dict-test";
        cmd.input.file = "./src/test/resources/read-test.txt";
        String inputContent = "cd";
        try {
            FsUtils.cpDir(Path.of(basePath, cmd.name), Path.of(basePath, cmd.name + ".backup"));

            var gramMap = repo.getGramMap(cmd.name);
            assertFalse(gramMap.containsKey(inputContent));
            assertFalse(gramMap.containsKey(String.valueOf(WORD_END)));

            assertEquals(0, cmd.call());
            assertEquals("Text read, dictionary " + cmd.name + " updated" + System.lineSeparator(), outStream.toString());

            gramMap = repo.getGramMap(cmd.name);
            assertTrue(gramMap.containsKey(inputContent));
            assertTrue(gramMap.containsKey(String.valueOf(WORD_END)));
        } finally {
            FsUtils.rmDir(Path.of(basePath, cmd.name));
            FsUtils.cpDir(Path.of(basePath, cmd.name + ".backup"), Path.of(basePath, cmd.name));
            FsUtils.rmDir(Path.of(basePath, cmd.name + ".backup"));
        }
    }

    @Test
    void call_notExists() {
        cmd.name = "i-do-no-exist";
        cmd.input.text = "abc";
        assertEquals(1, cmd.call());
        assertEquals("Dict not found: " + cmd.name + System.lineSeparator(), errStream.toString());
    }
}