package ukitinu.markovwords.repo;

import org.junit.jupiter.api.Test;
import ukitinu.markovwords.lib.DataException;
import ukitinu.markovwords.models.Dict;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static ukitinu.markovwords.AlphabetUtils.WORD_END;

class FileRepoIT {
    private final String basePath = "./src/test/resources/dict_dir";
    private final Repo repo = FileRepo.create(basePath);

    /**
     * Do not test exception message equality as it is OS-dependant.
     */
    @Test
    void createFileRepo() {
        var e1 = assertThrows(IllegalArgumentException.class, () -> FileRepo.create("./src/test/resources/not_exists"));
        assertTrue(e1.getMessage().endsWith("does not exist"));
        var e2 = assertThrows(IllegalArgumentException.class, () -> FileRepo.create("./src/test/resources/dict_dir/afile.txt"));
        assertTrue(e2.getMessage().endsWith("is not a directory"));

        FileRepo r1 = assertDoesNotThrow(() -> FileRepo.create("./src/test/resources/dict_dir"));
        FileRepo r2 = assertDoesNotThrow(() -> FileRepo.create("./src/test/resources/dict_dir/../dict_dir"));

        assertEquals(r1.getDataPath(), r2.getDataPath());
    }

    @Test
    void listAll_empty() throws IOException {
        String path = "./src/test/resources/empty_dir";
        try {
            Files.createDirectory(Path.of(path));
            Repo repo = FileRepo.create(path);
            assertTrue(repo.listAll().isEmpty());
        } finally {
            Files.delete(Path.of(path));
        }
    }

    @Test
    void listAll() throws IOException {
        assertEquals(2, repo.listAll().size());

        try {
            Files.createDirectory(Path.of(basePath + "/dict1"));
            Files.createDirectory(Path.of(basePath + "/dict2"));
            assertEquals(4, repo.listAll().size());
        } finally {
            Files.delete(Path.of(basePath + "/dict1"));
            Files.delete(Path.of(basePath + "/dict2"));
        }
    }

    @Test
    void get_ok() {
        String name = "dict-name";
        Dict dict = assertDoesNotThrow(() -> repo.get(name));
        assertEquals(name, dict.name());
        assertEquals(Set.of('a', 'b', 'c', '0', '1', '2', '\'', '.', '-', WORD_END), dict.alphabet());
    }

    @Test
    void get_notFound() {
        var e = assertThrows(DataException.class, () -> repo.get("not-found"));
        assertTrue(e.getCause() instanceof NoSuchFileException);

        assertThrows(DataException.class, () -> repo.get("bad-dict-dir"));
        assertThrows(DataException.class, () -> repo.get("bad-dict-name"));
    }

}