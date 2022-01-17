package ukitinu.markovwords.repo;

import ukitinu.markovwords.lib.DataException;
import ukitinu.markovwords.lib.Logger;
import ukitinu.markovwords.models.Dict;
import ukitinu.markovwords.models.Gram;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class FileRepo implements Repo {
    private static final Logger LOG = Logger.create(FileRepo.class);
    private static final String FILE_EXT = ".dat";
    private static final String DEL_PREFIX = ".";
    private static final String GRAM_DIR_SUFFIX = "-grams";

    private final Path dataPath;
    private final DataConverter dataConverter;

    static FileRepo create(String dataPath) {
        Path path = Path.of(dataPath);
        if (!Files.exists(path)) throw new IllegalArgumentException(path + " does not exist");
        if (!Files.isDirectory(path)) throw new IllegalArgumentException(path + " is not a directory");
        return new FileRepo(path);
    }

    private FileRepo(Path dataPath) {
        this.dataPath = dataPath.normalize();
        this.dataConverter = new DataConverter();
    }

    public Path getDataPath() {
        return dataPath;
    }

    /**
     * For every dict there is a directory with its name under {@link #dataPath}. Directories starting with
     * {@link #DEL_PREFIX} are ignored.
     */
    @Override
    public Collection<String> listAll() {
        try (var files = Files.list(dataPath)) {
            return files
                    .filter(Files::isDirectory)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .filter(s -> !s.startsWith(DEL_PREFIX))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOG.error("Unable to read dir {}: {}", dataPath.toString(), e.toString());
            throw new DataException("Unable to read data dir", e);
        }
    }

    /**
     * The dict's data are in the {@link #dataPath}/dict_name/dict_name.dat file.
     */
    @Override
    public Dict get(String name) {
        //TODO handling and test of deleted dict
        try {
            Path filePath = getDictFile(name);
            String content = Files.readString(filePath, StandardCharsets.UTF_8);
            return dataConverter.deserialiseDict(content);
        } catch (Exception e) {
            LOG.error("Unable to read dict {}: {}", name, e.toString());
            throw new DataException("Unable to read dict " + name, e);
        }
    }

    /**
     * The dict's directory is renamed adding a {@link #DEL_PREFIX} before it.
     */
    @Override
    public void delete(String name) {
        try {
            Path dirPath = getDictDir(name);
            Path deletedPath = getDeletedDictDir(name);
            Files.move(dirPath, deletedPath);
        } catch (Exception e) {
            LOG.error("Unable to delete dict {}: {}", name, e.toString());
            throw new DataException("Unable to delete dict " + name, e);
        }
    }

    @Override
    public Map<String, Gram> getGramMap(String name) {
        return null;
    }

    /**
     * Reads all the gram files of length {@param len} of dictionary {@param name}, converts them to {@link Gram}
     * and then returns a map containing them, with their value as key.
     *
     * @param name dict's name.
     * @param len  gram length.
     * @return gram.value-gram map of the given dictionary.
     */
    @Override
    public Map<String, Gram> getGramMap(String name, int len) {
        try {
            Dict dict = get(name);

            Path gramDir = getGramDir(name, len);
            return readGramContents(gramDir)
                    .stream()
                    .map(s -> dataConverter.deserialiseGram(s, dict))
                    .collect(Collectors.toMap(Gram::getValue, Function.identity()));

        } catch (Exception e) {
            LOG.error("Unable to get {}-grams of {}: {}", len, name, e.toString());
            throw new DataException("Unable to get " + len + "-grams of " + name, e);
        }
    }

    @Override
    public void update(Dict dict, Map<String, Gram> gramMap) {

    }

    /**
     * Converts to string every valid gram file's content.
     *
     * @param gramDir path to the gram's directory.
     * @return collection of gram files' contents.
     * @throws IOException if it fails to read the grams' directory or any of the gram files.
     */
    private Collection<String> readGramContents(Path gramDir) throws IOException {
        var paths = getGramPaths(gramDir);

        Collection<String> contents = new ArrayList<>(paths.size());
        for (var path : paths) contents.add(Files.readString(path, StandardCharsets.UTF_8));

        return contents;
    }

    /**
     * Collects all the paths of valid gram files.
     *
     * @param gramDir path to the gram's directory.
     * @return collection of gram files.
     * @throws IOException if it fails to read the directory's content.
     */
    private Collection<Path> getGramPaths(Path gramDir) throws IOException {
        try (var files = Files.list(gramDir)) {
            return files
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(FILE_EXT))
                    .toList();
        }
    }


    //region paths
    private Path getDictDir(String dictName) {
        return dataPath.resolve(dictName);
    }

    private Path getDeletedDictDir(String dictName) {
        return dataPath.resolve(DEL_PREFIX + dictName);
    }

    private Path getDictFile(String dictName) {
        return getDictDir(dictName).resolve(dictName + FILE_EXT);
    }

    private Path getGramDir(String dictName, int len) {
        return getDictDir(dictName).resolve(len + GRAM_DIR_SUFFIX);
    }
    //endregion
}
