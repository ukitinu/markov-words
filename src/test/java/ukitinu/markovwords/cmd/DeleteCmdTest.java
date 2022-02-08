package ukitinu.markovwords.cmd;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import ukitinu.markovwords.lib.FsUtils;
import ukitinu.markovwords.repo.FileRepo;
import ukitinu.markovwords.repo.Repo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DeleteCmdTest {
    private final String basePath = "./src/test/resources/dict_dir";
    private final Repo repo = FileRepo.create(basePath);
    private final ByteArrayOutputStream testStream = new ByteArrayOutputStream();
    private final DeleteCmd deleteCmd = new DeleteCmd(repo, new PrintStream(testStream));

    @Test
    void call() throws IOException {
        try {
            deleteCmd.name = "dict-name";
            assertEquals(0, deleteCmd.call());
            assertEquals("Dictionary deleted: " + deleteCmd.name + System.lineSeparator(), testStream.toString());
        } finally {
            FsUtils.cpDir(Path.of(basePath, "." + deleteCmd.name), Path.of(basePath, deleteCmd.name));
            FileUtils.deleteDirectory(Path.of(basePath, "." + deleteCmd.name).toFile());
        }
    }

    @Test
    void call_notFound() {
        deleteCmd.name = "not-found";
        assertEquals(1, deleteCmd.call());
        assertEquals("Unable to delete dict " + deleteCmd.name + System.lineSeparator(), testStream.toString());
    }

    @Test
    void call_deleted() {
        deleteCmd.name = ".del-dict";
        assertEquals(1, deleteCmd.call());
        assertEquals("Dict" + deleteCmd.name + "is already in deleted state" + System.lineSeparator(), testStream.toString());
    }
}