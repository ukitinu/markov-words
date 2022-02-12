package ukitinu.markovwords.repo;

import org.apache.commons.io.FileUtils;
import ukitinu.markovwords.lib.Couple;
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
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class FileRepo implements Repo {
    private static final Logger LOG = Logger.create(FileRepo.class);

    private final Path dataPath;
    private final DataConverter dataConverter;

    public static FileRepo create(String dataPath) {
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
     * For every dict there is a directory with its name under {@link #dataPath}. Directories "deleted" previously are
     * ignored.
     *
     * @see FilePaths to see how deleted directories are marked.
     */
    @Override
    public Couple<Collection<String>> listAll() {
        try (var files = Files.list(dataPath)) {
            Collection<String> names = files
                    .filter(Files::isDirectory)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .filter(s -> !FilePaths.isTemp(s))
                    .toList();
            Collection<String> visible = names.stream().filter(s -> !FilePaths.isDeleted(s)).toList();
            Collection<String> deleted = names.stream().filter(FilePaths::isDeleted).toList();
            return new Couple<>(visible, deleted);
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
            Path filePath = FilePaths.getDictFile(dataPath, name);
            String content = FsUtils.readFile(filePath);
            return dataConverter.deserialiseDict(content);
        } catch (NoSuchFileException e) {
            Path deletedPath = FilePaths.getDeletedDictDir(dataPath, name);
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
        try {
            Path gramPath = FilePaths.getGramFile(dataPath, dictName, gramValue);
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
     * If {@param permanent} is false, the dict's directory is renamed to mark is as deleted.
     * If {@param permanent} is true, the directory is deleted permanently.
     */
    @Override
    public void delete(String name, boolean permanent) {
        try {
            if (permanent) {
                FileUtils.deleteDirectory(FilePaths.getDictDir(dataPath, name).toFile());
                return;
            }

            if (FilePaths.isDeleted(name)) throw new DataException("Dict" + name + "is already in deleted state");

            Path dirPath = FilePaths.getDictDir(dataPath, name);
            Path deletedPath = FilePaths.getDeletedDictDir(dataPath, name);
            Files.move(dirPath, deletedPath);
        } catch (IOException e) {
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

            Path gramDir = FilePaths.getGramDir(dataPath, name, len);
            return readGramContents(gramDir)
                    .stream()
                    .map(s -> dataConverter.deserialiseGram(s, dict))
                    .collect(Collectors.toMap(Gram::getValue, Function.identity()));

        } catch (IOException e) {
            LOG.error("Unable to get {}-grams of {}: {}", len, name, e.toString());
            throw new DataException("Unable to get " + len + "-grams of " + name, e);
        }
    }

    /**
     * Writes the given data to the given dict, overriding any existing data if necessary.
     * First, the data is written to a tmp directory, then the original (if it exists) is replaced by the temp.
     * Every method called wraps any {@link IOException} as {@link DataException}, and throws it so that this method
     * fails as soon as an error occurs.
     *
     * @param dict    dictionary to update
     * @param gramMap gramMap to associate to the dict
     */
    @Override
    public void upsert(Dict dict, Map<String, Gram> gramMap) {
        createTempDictDir(dict);
        upsertDict(dict);
        upsertGramMap(gramMap, dict.name());
        replaceOriginalWithTemp(dict);
    }

    /**
     * Creates a temporary directory for the given dictionary, copying, if present, the current content.
     *
     * @param dict dictionary
     * @throws DataException if it fails to create the tmp directory.
     */
    private void createTempDictDir(Dict dict) {
        Path tmpDir = FilePaths.getDictDir(dataPath, dict.name(), true);
        try {
            if (Files.exists(tmpDir)) throw new DataException(tmpDir + " already exists, check " + dataPath);

            Path dictDir = FilePaths.getDictDir(dataPath, dict.name(), false);
            FsUtils.cpDir(dictDir, tmpDir);
        } catch (IOException e) {
            throw new DataException(String.format("Failed to create %s: %s", tmpDir, e.getMessage()), e);
        }
    }

    /**
     * Creates or overrides the dictionary's file.
     *
     * @param dict dictionary to update/create.
     * @throws DataException if an error occurs while writing the file or creating the directory.
     */
    private void upsertDict(Dict dict) {
        try {
            Path dictFile = FilePaths.getDictFile(dataPath, dict.name(), true);
            String dictString = dataConverter.serialiseDict(dict);
            FsUtils.writeToFile(dictFile, dictString);
        } catch (IOException e) {
            throw new DataException(String.format("Failed to upsert dictionary %s: %s", dict.name(), e.getMessage()), e);
        }
    }

    /**
     * Upserts every gram in the map.
     *
     * @param gramMap  map of the dictionary's grams.
     * @param dictName name of the dictionary.
     * @throws DataException if it fails to upsert a gram.
     * @see #upsertGram(Gram, String)
     */
    private void upsertGramMap(Map<String, Gram> gramMap, String dictName) {
        for (var gram : gramMap.values()) {
            upsertGram(gram, dictName);
        }
    }

    /**
     * Creates or updates the gram's file. If the file already exists, it reads its content and updates it only if
     * it is different.
     *
     * @param gram     gram to update/create.
     * @param dictName name of the gram's dictionary.
     * @throws DataException if an error occurs reading or writing the gram's file.
     */
    private void upsertGram(Gram gram, String dictName) {
        Path gramDir = FilePaths.getGramDir(dataPath, dictName, gram.getValue().length(), true);
        Path gramPath = FilePaths.getGramFile(dataPath, dictName, gram.getValue(), true);
        try {
            FsUtils.mkDir(gramDir);

            String gramString = dataConverter.serialiseGram(gram);
            if (!Files.exists(gramPath)) {
                FsUtils.writeToFile(gramPath, gramString);
            } else {
                String currentContent = FsUtils.readFile(gramPath);
                if (!currentContent.equals(gramString)) FsUtils.writeToFile(gramPath, gramString);
            }
        } catch (IOException e) {
            throw new DataException(String.format("Failed to upsert gram %s: %s", gramPath, e.getMessage()), e);
        }
    }

    /**
     * Replaces the original dict's directory with the temporary one that contains the new data.
     *
     * @param dict dictionary to update.
     * @throws DataException if it fails to copy the directory.
     */
    private void replaceOriginalWithTemp(Dict dict) {
        Path tmpDir = FilePaths.getDictDir(dataPath, dict.name(), true);
        Path dictDir = FilePaths.getDictDir(dataPath, dict.name(), false);
        try {
            FsUtils.moveAndReplace(tmpDir, dictDir);
        } catch (IOException e) {
            throw new DataException(String.format("Failed to replace %s with %s: %s", dictDir, tmpDir, e.getMessage()), e);
        }
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
                    .filter(FilePaths::isDataFile)
                    .toList();
        }
    }

}
