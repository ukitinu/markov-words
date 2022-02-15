package ukitinu.markovwords.repo;

import org.junit.jupiter.api.Test;
import ukitinu.markovwords.lib.FsUtils;
import ukitinu.markovwords.models.Dict;
import ukitinu.markovwords.models.Gram;

import java.io.IOException;
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
            assertTrue(repo.listAll().first().isEmpty());
            assertTrue(repo.listAll().second().isEmpty());
        } finally {
            Files.delete(Path.of(path));
        }
    }

    @Test
    void listAll() throws IOException {
        assertEquals(4, repo.listAll().first().size());
        assertEquals(2, repo.listAll().second().size());

        try {
            Files.createDirectory(Path.of(basePath, "dict1"));
            Files.createDirectory(Path.of(basePath, "dict2"));
            Files.createDirectory(Path.of(basePath, ".deleted_dict"));
            assertEquals(6, repo.listAll().first().size());
            assertEquals(3, repo.listAll().second().size());
        } finally {
            Files.delete(Path.of(basePath, "dict1"));
            Files.delete(Path.of(basePath, "dict2"));
            Files.delete(Path.of(basePath, ".deleted_dict"));
        }
    }

    @Test
    void exists() {
        assertTrue(repo.exists(".del-dict"));
        assertTrue(repo.exists("dict-name"));
        assertFalse(repo.exists("bad-dict-dir"));
        assertFalse(repo.exists("bad-dict-name"));
        assertFalse(repo.exists("i-do-not-exist"));
    }

    @Test
    void get_ok() {
        String name = "dict-name";
        Dict dict = assertDoesNotThrow(() -> repo.get(name));
        assertEquals(name, dict.name());
        assertTrue(dict.desc().isEmpty());
        assertEquals(Set.of('a', 'b', 'c', '0', '1', '2', '\'', '.', '-', WORD_END), dict.alphabet());
    }

    @Test
    void get_deleted() {
        String name = "del-dict";
        Dict dict = assertDoesNotThrow(() -> repo.get("." + name));
        assertEquals(name, dict.name());
        assertEquals("DELETED!", dict.desc());
        assertEquals(Set.of('a', 'b', 'c', 'd', 'e', 'f', 'g', WORD_END), dict.alphabet());
    }

    @Test
    void get_errors() {
        var e = assertThrows(DataException.class, () -> repo.get("not-found"));
        assertTrue(e.getCause() instanceof NoSuchFileException);

        assertThrows(DataException.class, () -> repo.get("bad-dict-dir"));
        assertThrows(DataException.class, () -> repo.get("bad-dict-name"));

        e = assertThrows(DataException.class, () -> repo.get("del-dict"));
        assertNull(e.getCause());
        assertTrue(e.getMessage().contains("Dict has been deleted: "));
    }

    @Test
    void hasGramMap() {
        assertThrows(IllegalArgumentException.class, ()->repo.hasGramMap("dict-name", -2));
        assertThrows(IllegalArgumentException.class, ()->repo.hasGramMap("dict-name", 0));

        assertFalse(repo.hasGramMap("dict-name", 1));
        assertFalse(repo.hasGramMap("dict-name", 2));
        assertFalse(repo.hasGramMap(".del-dict", 1));

        assertTrue(repo.hasGramMap("dict-complete", 1));
        assertTrue(repo.hasGramMap("dict-test", 2));
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
            Files.createDirectory(Path.of(basePath, name));
            Files.createFile(Path.of(basePath, name, name + ".dat"));

            assertEquals(2, repo.listAll().second().size());
            assertDoesNotThrow(() -> repo.delete(name, false));
            assertEquals(3, repo.listAll().second().size());

            assertTrue(Files.notExists(Path.of(basePath, name)));
            assertTrue(Files.exists(Path.of(basePath, "." + name)));
            assertTrue(Files.exists(Path.of(basePath, "." + name, name + ".dat")));
        } finally {
            FsUtils.rmDir(Path.of(basePath, name));
            FsUtils.rmDir(Path.of(basePath, "." + name));
        }
    }

    @Test
    void delete_deleted() {
        String name = ".del-dict";
        assertThrows(DataException.class, () -> repo.delete(name, false));
    }

    @Test
    void delete_notExisting() {
        assertThrows(DataException.class, () -> repo.delete("i-do-not-exist", false));
    }

    @Test
    void delete_permanent() throws IOException {
        String name = "delete-test";
        try {
            Files.createDirectory(Path.of(basePath, name));
            Files.createFile(Path.of(basePath, name, name + ".dat"));

            assertDoesNotThrow(() -> repo.delete(name, true));

            assertTrue(Files.notExists(Path.of(basePath, name)));
            assertTrue(Files.notExists(Path.of(basePath, "." + name)));
        } finally {
            FsUtils.rmDir(Path.of(basePath, name));
            FsUtils.rmDir(Path.of(basePath, "." + name));
        }
    }

    @Test
    void delete_permanentDeleted() throws IOException {
        String name = "delete-test";
        try {
            Files.createDirectory(Path.of(basePath, "." + name));
            Files.createFile(Path.of(basePath, "." + name, name + ".dat"));

            assertDoesNotThrow(() -> repo.delete("." + name, true));

            assertTrue(Files.notExists(Path.of(basePath, "." + name)));
            assertTrue(Files.notExists(Path.of(basePath, ".." + name)));
        } finally {
            FsUtils.rmDir(Path.of(basePath, "." + name));
            FsUtils.rmDir(Path.of(basePath, ".." + name));
        }
    }

    @Test
    void restore_failures() throws IOException {
        String name = "restore-test";
        try {
            Files.createDirectory(Path.of(basePath, name));
            Files.createFile(Path.of(basePath, name, name + ".dat"));

            assertThrows(DataException.class, () -> repo.restore(name));
            assertThrows(DataException.class, () -> repo.restore(".i-do-not-exist"));
        } finally {
            FsUtils.rmDir(Path.of(basePath + "/" + name));
        }
    }

    @Test
    void restore() throws IOException {
        String name = "restore-test";
        try {
            assertDoesNotThrow(() -> repo.restore("." + name));
            assertTrue(repo.exists(name));
            assertFalse(repo.exists("." + name));
            assertEquals(2, repo.getGramMap(name, 1).size());
            assertEquals(1, repo.getGramMap(name, 2).size());
        } finally {
            FsUtils.cpDir(Path.of(basePath, name), Path.of(basePath, "." + name));
            FsUtils.rmDir(Path.of(basePath, name));
        }
    }

    @Test
    void getGramMap() {
        String name = "dict-test";
        Map<String, Gram> gramMap = assertDoesNotThrow(() -> repo.getGramMap(name));
        assertEquals(3, gramMap.size());
        assertTrue(gramMap.containsKey("a"));
        assertTrue(gramMap.containsKey("ba"));
    }

    @Test
    void getGramMap_notExistent() {
        String name = "i-do-not-exist";
        assertThrows(DataException.class, () -> repo.getGramMap(name));
    }

    @Test
    void getGramMap_empty() {
        String name = "dict-name";
        Map<String, Gram> gramMap = assertDoesNotThrow(() -> repo.getGramMap(name));
        assertTrue(gramMap.isEmpty());
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
    void upsert_tmpAlreadyPresent() {
        Dict dict = new Dict("ups-no", Set.of());
        assertThrows(DataException.class, () -> repo.upsert(dict, Map.of()));
    }

    @Test
    void upsert_newDict() throws IOException {
        final Dict dict = new Dict("new", Set.of('a'));
        final Map<String, Gram> gramMap = Map.of(
                "a", new Gram("a", dict, Map.of('a', 1, '_', 2)),
                "_", new Gram("_", dict, Map.of('a', 2, '_', 1)),
                "a_", new Gram("a_", dict, Map.of('a', 1, '_', 2))
        );
        try {
            assertDoesNotThrow(() -> repo.upsert(dict, gramMap));

            assertEquals(dict, repo.get(dict.name()));

            assertEquals(2, repo.getGramMap(dict.name(), 1).size());
            assertEquals(1, repo.getGramMap(dict.name(), 2).size());

            assertTrue(Files.notExists(FilePaths.getDictDir(Path.of(basePath), dict.name(), true)));
        } finally {
            FsUtils.rmDir(FilePaths.getDictDir(Path.of(basePath), dict.name()));
        }
    }

    @Test
    void upsert_existing() throws IOException {
        String dictName = "existing";

        // update
        final Dict dict = new Dict(dictName, Set.of('a', 'b', 'c'));
        final Map<String, Gram> gramMap = Map.of(
                "a", new Gram("a", dict, Map.of('a', 3, 'b', 2, '_', 2, 'c', 2)),
                "c", new Gram("c", dict, Map.of('b', 2, 'c', 4, '_', 1)),
                "cb", new Gram("cb", dict, Map.of('a', 1, '_', 1)),
                "ba", new Gram("ba", dict, Map.of('a', 1, '_', 4))
        );

        try {
            // moving dir to repo path, to have consistent test data
            FsUtils.cpDir(Path.of("./src/test/resources/existing"), Path.of(basePath, dictName));

            // check existing
            var oneGrams = repo.getGramMap(dictName, 1);
            assertFalse(oneGrams.containsKey("c"));
            assertEquals(4, oneGrams.get("a").get('a'));
            assertEquals(0, oneGrams.get("a").get('c'));

            var twoGrams = repo.getGramMap(dictName, 2);
            assertFalse(twoGrams.containsKey("cb"));
            assertEquals(10, twoGrams.get("ba").getWeight());
            assertEquals(3, twoGrams.get("ba").get('a'));
            assertEquals(4, twoGrams.get("ba").get('_'));

            // update and check
            assertDoesNotThrow(() -> repo.upsert(dict, gramMap));
            oneGrams = repo.getGramMap(dictName, 1);
            assertTrue(oneGrams.containsKey("b"));

            assertEquals(3, oneGrams.get("a").get('a'));
            assertEquals(2, oneGrams.get("a").get('c'));

            assertEquals(2, oneGrams.get("c").get('b'));
            assertEquals(4, oneGrams.get("c").get('c'));
            assertEquals(1, oneGrams.get("c").get('_'));

            twoGrams = repo.getGramMap(dictName, 2);
            assertEquals(5, twoGrams.get("ba").getWeight());
            assertEquals(1, twoGrams.get("ba").get('a'));
            assertEquals(4, twoGrams.get("ba").get('_'));

            assertEquals(1, twoGrams.get("cb").get('a'));
            assertEquals(1, twoGrams.get("cb").get('_'));
        } finally {
            // remove edited dict
            FsUtils.rmDir(FilePaths.getDictDir(Path.of(basePath), dictName));
        }
    }


}