package ukitinu.markovwords.cmd;

import org.junit.jupiter.api.Test;
import ukitinu.markovwords.lib.FsUtils;
import ukitinu.markovwords.models.Dict;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

final class RenameCmdIT extends CmdITHelper {
    private final RenameCmd cmd = new RenameCmd(repo, new PrintStream(testStream));

    @Test
    void call() throws IOException {
        cmd.name = "dict-test";
        cmd.newName = "new-dict";
        try {
            FsUtils.cpDir(Path.of(basePath, cmd.name), Path.of(basePath, cmd.name + ".backup"));

            assertFalse(repo.exists(cmd.newName));

            assertEquals(0, cmd.call());
            assertEquals("Dictionary renamed from " + cmd.name + " to " + cmd.newName + System.lineSeparator(), testStream.toString());
            assertEquals(new Dict(cmd.newName, Set.of('a', 'b', 'c', 'd')), repo.get(cmd.newName));
            assertEquals(3, repo.getGramMap(cmd.newName).size());
            assertFalse(repo.exists(cmd.name));
        } finally {
            FsUtils.cpDir(Path.of(basePath, cmd.name + ".backup"), Path.of(basePath, cmd.name));
            FsUtils.rmDir(Path.of(basePath, cmd.name + ".backup"));
            FsUtils.rmDir(Path.of(basePath, cmd.newName));
        }
    }

    @Test
    void call_newExisting() {
        cmd.name = "old-name";
        cmd.newName = "dict-name";
        assertEquals(1, cmd.call());
        assertEquals("New name " + cmd.newName + " is already in use" + System.lineSeparator(), testStream.toString());
    }

    @Test
    void call_oldNotExisting() {
        cmd.name = "old-name";
        cmd.newName = "new-name";
        assertEquals(1, cmd.call());
        assertEquals("Dict not found: " + cmd.name + System.lineSeparator(), testStream.toString());
    }

}