package ukitinu.markovwords.repo;

import ukitinu.markovwords.lib.FsUtils;
import ukitinu.markovwords.lib.Logger;
import ukitinu.markovwords.models.Dict;
import ukitinu.markovwords.models.Gram;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class FileRepo implements Repo {
    private static final Logger LOG = Logger.create(FileRepo.class);
    private static final String FILE_EXT = ".dat";
    private static final String DEL_PREFIX = ".";
    private static final String GRAM_DIR_SUFFIX = "-grams";
    private static final String TMP_SUFFIX = ".tmp";

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
        try {
            if (name.startsWith(DEL_PREFIX)) {
                LOG.info("Cannot get deleted dict {}", name);
                throw new DataException("Cannot get deleted dict " + name);
            }

            Path filePath = getDictFile(name);
            String content = FsUtils.readFile(filePath);
            return dataConverter.deserialiseDict(content);
        } catch (NoSuchFileException e) {
            Path deletedPath = getDeletedDictDir(name);
            if (Files.isDirectory(deletedPath)) {
                LOG.error("Dict {} has been deleted", name);
                throw new DataException("Dict has been deleted: " + name);
            } else {
                LOG.error("Dict {} not found (either dir or .dat file)", name);
                throw new DataException("Dict not found: " + name, e);
            }
        } catch (IOException e) {
            LOG.error("Unable to read dict {}: {}", name, e.toString());
            throw new DataException("Unable to read dict " + name, e);
        }
    }

    @Override
    public Gram getGram(String dictName, String gramValue) {
        Dict dict = get(dictName);
        int len = gramValue.length();
        try {
            Path gramPath = getGramDir(dictName, len).resolve(gramValue + FILE_EXT);
            String content = FsUtils.readFile(gramPath);
            return dataConverter.deserialiseGram(content, dict);
        } catch (NoSuchFileException e) {
            LOG.error("Gram {} of Dict {} not found", gramValue, dictName);
            throw new DataException("Gram " + gramValue + " of Dict " + dictName + " not found");
        } catch (IOException e) {
            LOG.error("Unable to read gram {} of dict {}: {}", gramValue, dictName, e.toString());
            throw new DataException("Unable to read gram " + gramValue + " of Dic " + dictName, e);
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
    public void upsert(Dict dict, Map<String, Gram> gramMap) {

    }

    /**
     * Creates a temporary directory for the given dictionary, copying, if present, the current content.
     *
     * @param dict dictionary
     * @throws IOException if it fails to create the tmp directory.
     */
    private void createTempDictDir(Dict dict) throws IOException {
        Path tmpDir = getDictDir(dict.name(), true);
        if (Files.exists(tmpDir)) throw new DataException(tmpDir + " already exists, check " + dataPath);

        Path dictDir = getDictDir(dict.name(), false);
        FsUtils.cpDir(dictDir, tmpDir);
    }

    /**
     * Creates, if necessary, the dict's directory, and then creates or overrides the dict's file.
     *
     * @param dict dictionary to update/create.
     * @throws IOException if an error occurs while writing the file or creating the directory.
     */
    private void upsertDict(Dict dict) throws IOException {
        FsUtils.mkDir(getDictDir(dict.name()));

        Path dictFile = getDictFile(dict.name());
        String dictString = dataConverter.serialiseDict(dict);
        FsUtils.writeToFile(dictFile, dictString);
    }

    private void upsertGramMap(Map<String, Gram> gramMap, String dictName) throws IOException {
        Map<Integer, List<Gram>> gramsByLength = gramMap
                .values()
                .stream()
                .collect(Collectors.groupingBy(g -> g.getValue().length()));
        for (var entry : gramsByLength.entrySet()) {
            Path gramDir = getGramDir(dictName, entry.getKey());
            for (var gram : entry.getValue()) upsertGram(gram, gramDir);
        }
    }

    /**
     * Creates or updates the gram's file. If the file already exists, it reads its content and updates it only if
     * it is different.
     *
     * @param gram    gram to update/create.
     * @param gramDir directory where the gram should be stored.
     * @throws IOException if an error occurs reading or writing the gram's file.
     */
    private void upsertGram(Gram gram, Path gramDir) throws IOException {
        Path gramPath = gramDir.resolve(gram.getValue());
        String gramString = dataConverter.serialiseGram(gram);
        if (!Files.exists(gramPath)) {
            FsUtils.writeToFile(gramPath, gramString);
        } else {
            String currentContent = FsUtils.readFile(gramPath);
            if (!currentContent.equals(gramString)) FsUtils.writeToFile(gramPath, gramString);
        }
    }

    //region utils

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
        for (var path : paths) contents.add(FsUtils.readFile(path));

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
    //endregion


    //region paths
    private Path getDictDir(String dictName) {
        return getDictDir(dictName, false);
    }

    private Path getDictDir(String dictName, boolean isTemp) {
        return dataPath.resolve(dictName + (isTemp ? TMP_SUFFIX : ""));
    }

    private Path getDeletedDictDir(String dictName) {
        return dataPath.resolve(DEL_PREFIX + dictName);
    }

    private Path getDictFile(String dictName) {
        return getDictDir(dictName).resolve(dictName + FILE_EXT);
    }

    private Path getDictFile(String dictName, boolean isTemp) {
        return getDictDir(dictName, isTemp).resolve(dictName + FILE_EXT);
    }

    private Path getGramDir(String dictName, int len) {
        return getDictDir(dictName).resolve(len + GRAM_DIR_SUFFIX);
    }

    private Path getGramDir(String dictName, int len, boolean isTemp) {
        return getDictDir(dictName, isTemp).resolve(len + GRAM_DIR_SUFFIX);
    }
    //endregion
}
