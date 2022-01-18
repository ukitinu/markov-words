package ukitinu.markovwords.repo;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import ukitinu.markovwords.models.Dict;
import ukitinu.markovwords.models.Gram;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Map;
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
        assertEquals(3, repo.listAll().size());

        try {
            Files.createDirectory(Path.of(basePath + "/dict1"));
            Files.createDirectory(Path.of(basePath + "/dict2"));
            Files.createDirectory(Path.of(basePath + "/.deleted_dict"));
            assertEquals(5, repo.listAll().size());
        } finally {
            Files.delete(Path.of(basePath + "/dict1"));
            Files.delete(Path.of(basePath + "/dict2"));
            Files.delete(Path.of(basePath + "/.deleted_dict"));
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
    void get_errors() {
        var e = assertThrows(DataException.class, () -> repo.get("not-found"));
        assertTrue(e.getCause() instanceof NoSuchFileException);

        assertThrows(DataException.class, () -> repo.get("bad-dict-dir"));
        assertThrows(DataException.class, () -> repo.get("bad-dict-name"));

        e = assertThrows(DataException.class, () -> repo.get("del-dict"));
        assertNull(e.getCause());

        assertThrows(DataException.class, () -> repo.get(".del-dict"));
    }

    @Test
    void getGram_ok() {
        String dictName = "dict-test";
        String gram1 = "a";
        String gram2 = "ba";
        var g1 = assertDoesNotThrow(() -> repo.getGram(dictName, gram1));
        var g2 = assertDoesNotThrow(() -> repo.getGram(dictName, gram2));
        assertEquals("a", g1.getValue());
        assertEquals(dictName, g1.getDict().name());
        assertEquals("ba", g2.getValue());
        assertEquals(Map.of('a', 3, 'b', 3, WORD_END, 4), g2.getCharMap());
    }

    @Test
    void getGram_dictErrors() {
        String delDict = "del-dict";
        String delDictDot = ".del-dict";
        String notFoundDict = "not-found-dict";
        assertThrows(DataException.class, () -> repo.getGram(delDict, "value"));
        assertThrows(DataException.class, () -> repo.getGram(delDictDot, "value"));
        assertThrows(DataException.class, () -> repo.getGram(notFoundDict, "value"));
    }

    @Test
    void getGram_gramErrors() {
        String dictName = "dict-test";
        String dirNotFound = "dirnotfound";
        String gramNotFound = "x";
        assertThrows(DataException.class, () -> repo.getGram(dictName, dirNotFound));
        assertThrows(DataException.class, () -> repo.getGram(dictName, gramNotFound));
    }

    @Test
    void delete() throws IOException {
        String name = "delete-test";
        try {
            Files.createDirectory(Path.of(basePath + "/" + name));
            Files.createFile(Path.of(basePath + "/" + name + "/" + name));

            assertDoesNotThrow(() -> repo.delete(name));

            assertTrue(Files.notExists(Path.of(basePath + "/" + name)));
            assertTrue(Files.exists(Path.of(basePath + "/." + name)));
            assertTrue(Files.exists(Path.of(basePath + "/." + name + "/" + name)));
        } finally {
            FileUtils.deleteDirectory(Path.of(basePath + "/" + name).toFile());
            FileUtils.deleteDirectory(Path.of(basePath + "/." + name).toFile());
        }
    }

    @Test
    void getGramMap_1() {
        String name = "dict-test";
        Map<String, Gram> gramMap = assertDoesNotThrow(() -> repo.getGramMap(name, 1));
        assertEquals(2, gramMap.size());
    }

    @Test
    void getGramMap_2() {
        String name = "dict-test";
        Map<String, Gram> gramMap = assertDoesNotThrow(() -> repo.getGramMap(name, 2));
        assertEquals(1, gramMap.size());
    }

    @Test
    void getGramMap_3() {
        String name = "dict-test";
        assertThrows(DataException.class, () -> repo.getGramMap(name, 3));
    }

    @Test
    void upsert() throws IOException {
    }


}