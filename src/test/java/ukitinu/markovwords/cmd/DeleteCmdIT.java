package ukitinu.markovwords.cmd;

import org.junit.jupiter.api.Test;
import ukitinu.markovwords.lib.FsUtils;
import ukitinu.markovwords.repo.FileRepo;
import ukitinu.markovwords.repo.Repo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DeleteCmdIT {
    private final String basePath = "./src/test/resources/dict_dir";
    private final Repo repo = FileRepo.create(basePath);
    private final ByteArrayOutputStream testStream = new ByteArrayOutputStream();
    private final DeleteCmd deleteCmd = new DeleteCmd(repo, new PrintStream(testStream));

    //TODO test permanent deletion

    @Test
    void call() throws IOException {
        try {
            deleteCmd.name = "dict-name";
            assertEquals(0, deleteCmd.call());
            assertEquals("Dictionary deleted: " + deleteCmd.name + System.lineSeparator(), testStream.toString());
        } finally {
            FsUtils.cpDir(Path.of(basePath, "." + deleteCmd.name), Path.of(basePath, deleteCmd.name));
            FsUtils.rmDir(Path.of(basePath, "." + deleteCmd.name));
        }
    }

    @Test
    void call_notFound() {
        deleteCmd.name = "not-found";
        assertEquals(1, deleteCmd.call());
        assertEquals("Dict not found: " + deleteCmd.name + System.lineSeparator(), testStream.toString());
    }

    @Test
    void call_deleted() {
        deleteCmd.name = ".del-dict";
        assertEquals(1, deleteCmd.call());
        assertEquals("Dict " + deleteCmd.name + " is already in deleted state" + System.lineSeparator()
                        + DeleteCmd.DEL_PERM_HINT + System.lineSeparator(),
                testStream.toString());
    }

    @Test
    void call_deletedNoDot() {
        deleteCmd.name = "del-dict";
        assertEquals(1, deleteCmd.call());
        assertEquals("Dict has been deleted: " + deleteCmd.name + System.lineSeparator()
                        + "Use ." + deleteCmd.name + " to " + DeleteCmd.DEL_PERM_HINT + System.lineSeparator(),
                testStream.toString());
    }
}