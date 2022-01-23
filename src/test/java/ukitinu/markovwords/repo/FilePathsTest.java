package ukitinu.markovwords.repo;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FilePathsTest {
    private final Path path = Path.of("/base/path");
    private final String dictName = "dict-name";
    private final String dictPath = path + "/" + dictName;

    @Test
    void getDictDir() {
        assertEquals(Path.of(dictPath), FilePaths.getDictDir(path, dictName));
        assertEquals(Path.of(dictPath), FilePaths.getDictDir(path, dictName, false));
        assertEquals(Path.of(dictPath + ".tmp"), FilePaths.getDictDir(path, dictName, true));
    }

    @Test
    void getDeletedDictDir() {
        assertEquals(Path.of(path + "/." + dictName), FilePaths.getDeletedDictDir(path, dictName));
    }

    @Test
    void getDictFile() {
        assertEquals(Path.of(dictPath + "/" + dictName + ".dat"), FilePaths.getDictFile(path, dictName));
        assertEquals(Path.of(dictPath + "/" + dictName + ".dat"), FilePaths.getDictFile(path, dictName, false));
        assertEquals(Path.of(dictPath + ".tmp/" + dictName + ".dat"), FilePaths.getDictFile(path, dictName, true));
    }

    @Test
    void getGramDir() {
        int len = 123;
        assertEquals(Path.of(dictPath + "/" + len + "-grams"), FilePaths.getGramDir(path, dictName, len));
        assertEquals(Path.of(dictPath + "/" + len + "-grams"), FilePaths.getGramDir(path, dictName, len, false));
        assertEquals(Path.of(dictPath + ".tmp/" + len + "-grams"), FilePaths.getGramDir(path, dictName, len, true));
    }

    @Test
    void getGramFile() {
        String gramValue = "gram";
        int len = gramValue.length();
        assertEquals(
                Path.of(dictPath + "/" + len + "-grams/" + gramValue + ".dat"),
                FilePaths.getGramFile(path, dictName, gramValue)
        );
        assertEquals(
                Path.of(dictPath + "/" + len + "-grams/" + gramValue + ".dat"),
                FilePaths.getGramFile(path, dictName, gramValue, false)
        );
        assertEquals(
                Path.of(dictPath + ".tmp" + "/" + len + "-grams/" + gramValue + ".dat"),
                FilePaths.getGramFile(path, dictName, gramValue, true)
        );
    }

    @Test
    void isDeleted() {
        assertTrue(FilePaths.isDeleted(".deleted"));
        assertFalse(FilePaths.isDeleted("not-deleted"));
    }

    @Test
    void isDataFile() {
        assertTrue(FilePaths.isDataFile(Path.of("file.dat")));
        assertFalse(FilePaths.isDataFile(Path.of("file")));
    }
}