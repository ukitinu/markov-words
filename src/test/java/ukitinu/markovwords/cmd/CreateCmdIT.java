package ukitinu.markovwords.cmd;

import org.junit.jupiter.api.Test;
import ukitinu.markovwords.lib.FsUtils;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

final class CreateCmdIT extends CmdITHelper {
    private final CreateCmd cmd = new CreateCmd(repo, new PrintStream(testStream));

    @Test
    void call() throws IOException {
        cmd.name = "new-dict";
        cmd.alphabet = "abc";
        try {
            assertFalse(repo.exists(cmd.name));
            assertEquals(0, cmd.call());
            assertEquals("New dictionary created: " + cmd.name + System.lineSeparator(), testStream.toString());
            assertTrue(repo.exists(cmd.name));
        } finally {
            FsUtils.rmDir(Path.of(basePath, cmd.name));
        }
    }

    @Test
    void call_alreadyExists() {
        cmd.name = "dict-name";
        assertEquals(1, cmd.call());
        assertEquals("There is already a dictionary named " + cmd.name + System.lineSeparator(), testStream.toString());
    }
}