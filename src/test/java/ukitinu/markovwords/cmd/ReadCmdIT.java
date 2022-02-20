package ukitinu.markovwords.cmd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ukitinu.markovwords.Ingester;
import ukitinu.markovwords.lib.FsUtils;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static ukitinu.markovwords.AlphabetUtils.WORD_END;

final class ReadCmdIT extends CmdITHelper {
    private final ReadCmd cmd = new ReadCmd(repo, new PrintStream(testStream), new Ingester());

    @BeforeEach
    void setUp() {
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
            assertEquals("Text read, dictionary " + cmd.name + " updated" + System.lineSeparator(), testStream.toString());

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
            assertEquals("Text read, dictionary " + cmd.name + " updated" + System.lineSeparator(), testStream.toString());

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
        assertEquals("Dict not found: " + cmd.name + System.lineSeparator(), testStream.toString());
    }
}