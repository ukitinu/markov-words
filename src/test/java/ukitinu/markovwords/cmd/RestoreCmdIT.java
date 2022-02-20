package ukitinu.markovwords.cmd;

import org.junit.jupiter.api.Test;
import ukitinu.markovwords.lib.FsUtils;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class RestoreCmdIT extends CmdITHelper {
    private final RestoreCmd cmd = new RestoreCmd(repo, new PrintStream(outStream), new PrintStream(errStream));

    @Test
    void call() throws IOException {
        String name = "restore-test";
        cmd.name = "." + name;
        try {
            assertEquals(0, cmd.call());
            assertEquals("Dictionary restored: " + cmd.name + System.lineSeparator(), outStream.toString());
            assertTrue(repo.exists(name));
        } finally {
            FsUtils.cpDir(Path.of(basePath, name), Path.of(basePath, cmd.name));
            FsUtils.rmDir(Path.of(basePath, name));
        }
    }

    @Test
    void call_notExisting() {
        cmd.name = "i-do-not-exist";
        assertEquals(1, cmd.call());
        assertEquals("given dictionary does not exists: " + cmd.name + System.lineSeparator(), errStream.toString());
    }

    @Test
    void call_notDeleted() {
        cmd.name = "dict-name";
        assertEquals(1, cmd.call());
        assertEquals("Dict " + cmd.name + " is not in deleted state" + System.lineSeparator(), errStream.toString());
    }
}