package ukitinu.markovwords.repo;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileRepoIT {

    @Test
    void listAll_noDir() {
        Repo repo = new FileRepo("./src/test/resources/i_do_not_exist");
        assertTrue(repo.listAll().isEmpty());
    }

    @Test
    void listAll_empty() throws IOException {
        String path = "./src/test/resources/empty_dir";
        try {
            Files.createDirectory(Path.of(path));
            Repo repo = new FileRepo(path);
            assertTrue(repo.listAll().isEmpty());
        } finally {
            Files.delete(Path.of(path));
        }
    }

    @Test
    void listAll() throws IOException {
        String path = "./src/test/resources/test_dict_dir";
        Repo repo = new FileRepo(path);
        assertTrue(repo.listAll().isEmpty());

        try {
            Files.createDirectory(Path.of(path + "/dict1"));
            Files.createDirectory(Path.of(path + "/dict2"));
            assertEquals(2, repo.listAll().size());
        } finally {
            Files.delete(Path.of(path + "/dict1"));
            Files.delete(Path.of(path + "/dict2"));
        }
    }

}