package ukitinu.markovwords.cmd;

import org.junit.jupiter.api.Test;
import ukitinu.markovwords.lib.FsUtils;
import ukitinu.markovwords.models.Dict;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

final class UpdateCmdIT extends CmdITHelper {
    private final UpdateCmd cmd = new UpdateCmd(repo, new PrintStream(testStream));

    @Test
    void call_changeName() throws IOException {
        cmd.name = "dict-test";
        cmd.newName = "new-dict";
        try {
            FsUtils.cpDir(Path.of(basePath, cmd.name), Path.of(basePath, cmd.name + ".backup"));

            assertFalse(repo.exists(cmd.newName));

            assertEquals(0, cmd.call());

            assertEquals("Dictionary updated" + System.lineSeparator()
                            + "name: " + cmd.name + " -> " + cmd.newName + System.lineSeparator(),
                    testStream.toString());

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
    void call_changeDesc() throws IOException {
        cmd.name = "dict-test";
        cmd.newDesc = "new description!";
        try {
            FsUtils.cpDir(Path.of(basePath, cmd.name), Path.of(basePath, cmd.name + ".backup"));

            assertNotEquals(cmd.newDesc, repo.get(cmd.name).desc());
            assertEquals(0, cmd.call());
            assertEquals(cmd.newDesc, repo.get(cmd.name).desc());

            assertEquals("Dictionary updated" + System.lineSeparator()
                            + "description: description:test -> " + cmd.newDesc + System.lineSeparator(),
                    testStream.toString());
        } finally {
            FsUtils.rmDir(Path.of(basePath, cmd.name));
            FsUtils.cpDir(Path.of(basePath, cmd.name + ".backup"), Path.of(basePath, cmd.name));
            FsUtils.rmDir(Path.of(basePath, cmd.name + ".backup"));
        }
    }

    @Test
    void call() throws IOException {
        cmd.name = "dict-test";
        cmd.newDesc = "The description is \"different\"";
        cmd.newName = "new-dict";
        try {
            FsUtils.cpDir(Path.of(basePath, cmd.name), Path.of(basePath, cmd.name + ".backup"));

            assertFalse(repo.exists(cmd.newName));

            assertEquals(0, cmd.call());

            assertEquals("Dictionary updated" + System.lineSeparator()
                            + "name: " + cmd.name + " -> " + cmd.newName + System.lineSeparator()
                            + "description: description:test -> " + cmd.newDesc + System.lineSeparator(),
                    testStream.toString());

            assertEquals(new Dict(cmd.newName, Set.of('a', 'b', 'c', 'd')), repo.get(cmd.newName));
            assertEquals(cmd.newDesc, repo.get(cmd.newName).desc());
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
        assertEquals("new name " + cmd.newName + " is already in use" + System.lineSeparator(), testStream.toString());
    }

    @Test
    void call_oldNotExisting() {
        cmd.name = "old-name";
        cmd.newName = "new-name";
        assertEquals(1, cmd.call());
        assertEquals("Dict not found: " + cmd.name + System.lineSeparator(), testStream.toString());
    }

    @Test
    void call_noOptions() {
        cmd.name = "dict-test";
        assertEquals(1, cmd.call());
        assertEquals("missing option: at least one of --new-name or --new-desc must be specified" + System.lineSeparator(),
                testStream.toString());
    }

    @Test
    void call_invalidName() {
        cmd.name = "old-name";
        cmd.newName = "_invalid";
        assertEquals(1, cmd.call());
        assertEquals("dict name must consist of english letters, digits and dashes and must start with a letter" + System.lineSeparator(), testStream.toString());
    }

    @Test
    void call_invalidDesc() {
        cmd.name = "old-name";
        cmd.newDesc = "invalid\n";
        assertEquals(1, cmd.call());
        assertEquals("dict desc must consist of english letters, digits, whitespace and punctuation only" + System.lineSeparator(), testStream.toString());
    }

}