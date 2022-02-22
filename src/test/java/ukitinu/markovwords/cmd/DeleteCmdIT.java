package ukitinu.markovwords.cmd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ukitinu.markovwords.lib.FsUtils;
import ukitinu.markovwords.models.Dict;
import ukitinu.markovwords.repo.FileRepo;
import ukitinu.markovwords.repo.Repo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class DeleteCmdIT {
    private final String basePath = "./src/test/resources/dict_dir";
    private final Repo repo = FileRepo.create(basePath);
    private final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errStream = new ByteArrayOutputStream();

    private final DeleteCmd cmd = new DeleteCmd();

    @BeforeEach
    void setUp() {
        cmd.redirect(repo, new PrintStream(outStream), new PrintStream(errStream));
    }

    @Test
    void call() throws IOException {
        try {
            cmd.name = "dict-name";
            assertEquals(0, cmd.call());
            assertEquals("Dictionary deleted: " + cmd.name + System.lineSeparator(), outStream.toString());
            assertTrue(Files.exists(Path.of(basePath + "/." + cmd.name)));
        } finally {
            FsUtils.cpDir(Path.of(basePath, "." + cmd.name), Path.of(basePath, cmd.name));
            FsUtils.rmDir(Path.of(basePath, "." + cmd.name));
        }
    }

    @Test
    void call_notFound() {
        cmd.name = "not-found";
        assertEquals(1, cmd.call());
        assertEquals("Dict not found: " + cmd.name + System.lineSeparator(), errStream.toString());
    }

    @Test
    void call_deleted() {
        cmd.name = ".del-dict";
        assertEquals(1, cmd.call());
        assertEquals("Dict " + cmd.name + " is already in deleted state" + System.lineSeparator()
                        + DeleteCmd.DEL_PERM_HINT + System.lineSeparator(),
                errStream.toString());
    }

    @Test
    void call_deletedNoDot() {
        cmd.name = "del-dict";
        assertEquals(1, cmd.call());
        assertEquals("Dict has been deleted: " + cmd.name + System.lineSeparator()
                        + "Use ." + cmd.name + " to " + DeleteCmd.DEL_PERM_HINT + System.lineSeparator(),
                errStream.toString());
    }

    @Test
    void call_permanent() throws IOException {
        cmd.name = "delete-test";
        cmd.permanent = true;
        try {
            repo.upsert(new Dict(cmd.name, Set.of()), Map.of());
            assertTrue(repo.exists(cmd.name));
            assertEquals(0, cmd.call());
            assertEquals("Dictionary deleted permanently: " + cmd.name + System.lineSeparator(), outStream.toString());
            assertTrue(Files.notExists(Path.of(basePath + "/" + cmd.name)));
            assertTrue(Files.notExists(Path.of(basePath + "/." + cmd.name)));
        } finally {
            FsUtils.rmDir(Path.of(basePath + "/" + cmd.name));
            FsUtils.rmDir(Path.of(basePath + "/." + cmd.name));
        }
    }

    @Test
    void call_permanentDeleted() throws IOException {
        String name = "delete-test";
        cmd.name = "." + name;
        cmd.permanent = true;
        try {
            Files.createDirectory(Path.of(basePath + "/." + name));
            Files.createFile(Path.of(basePath + "/." + name + "/" + name + ".dat"));

            assertTrue(repo.exists(cmd.name));
            assertEquals(0, cmd.call());
            assertEquals("Dictionary deleted permanently: " + cmd.name + System.lineSeparator(), outStream.toString());
            assertTrue(Files.notExists(Path.of(basePath + "/" + cmd.name)));
        } finally {
            FsUtils.rmDir(Path.of(basePath + "/" + cmd.name));
        }
    }
}