package ukitinu.markovwords.repo;

import java.nio.file.Path;

final class FilePaths {
    private static final String FILE_EXT = ".dat";
    private static final String DEL_PREFIX = ".";
    private static final String GRAM_DIR_SUFFIX = "-grams";
    private static final String TMP_SUFFIX = ".tmp";

    private FilePaths() {
        throw new IllegalStateException("non-instantiable");
    }


    static Path getDictDir(Path dataPath, String dictName) {
        return getDictDir(dataPath, dictName, false);
    }

    static Path getDictDir(Path dataPath, String dictName, boolean isTemp) {
        return dataPath.resolve(dictName + (isTemp ? TMP_SUFFIX : ""));
    }

    static Path getDeletedDictDir(Path dataPath, String dictName) {
        return dataPath.resolve(DEL_PREFIX + dictName);
    }

    static Path getDictFile(Path dataPath, String dictName) {
        return getDictDir(dataPath, dictName, false).resolve(dictName + FILE_EXT);
    }

    static Path getDictFile(Path dataPath, String dictName, boolean isTemp) {
        return getDictDir(dataPath, dictName, isTemp).resolve(dictName + FILE_EXT);
    }

    static Path getGramDir(Path dataPath, String dictName, int len) {
        return getDictDir(dataPath, dictName, false).resolve(len + GRAM_DIR_SUFFIX);
    }

    static Path getGramDir(Path dataPath, String dictName, int len, boolean isTemp) {
        return getDictDir(dataPath, dictName, isTemp).resolve(len + GRAM_DIR_SUFFIX);
    }

    static Path getGramFile(Path dataPath, String dictName, String gramValue) {
        int len = gramValue.length();
        return getGramDir(dataPath, dictName, len, false).resolve(gramValue + FILE_EXT);
    }

    static Path getGramFile(Path dataPath, String dictName, String gramValue, boolean isTemp) {
        int len = gramValue.length();
        return getGramDir(dataPath, dictName, len, isTemp).resolve(gramValue + FILE_EXT);
    }

    static boolean isDeleted(String filename) {
        return filename.startsWith(DEL_PREFIX);
    }

    static boolean isDataFile(Path path) {
        return path.toString().endsWith(FILE_EXT);
    }
}
