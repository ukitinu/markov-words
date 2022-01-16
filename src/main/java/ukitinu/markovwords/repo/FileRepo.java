package ukitinu.markovwords.repo;

import ukitinu.markovwords.lib.DataException;
import ukitinu.markovwords.lib.Logger;
import ukitinu.markovwords.models.Dict;
import ukitinu.markovwords.models.Gram;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public final class FileRepo implements Repo {
    private static final Logger LOG = Logger.create(FileRepo.class);
    private static final String FILE_EXT = ".dat";
    private static final String DEL_PREFIX = ".";

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
     * For every dict there is a directory with its name under {@link #dataPath}.
     */
    @Override
    public Collection<String> listAll() {
        try (var stream = Files.list(dataPath)) {
            return stream
                    .filter(Files::isDirectory)
                    .map(Path::getFileName)
                    .map(Path::toString)
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
            Path filePath = dataPath.resolve(name).resolve(name + FILE_EXT);
            String content = Files.readString(filePath, StandardCharsets.UTF_8);
            return dataConverter.deserialiseDict(content);
        } catch (Exception e) {
            LOG.error("Unable to read dict {}: {}", name, e.toString());
            throw new DataException("Unable to read dict " + name, e);
        }
    }

    @Override
    public void delete(String name) {
        try {
            Path dirPath = dataPath.resolve(name);
            Path deletedPath = dataPath.resolve(DEL_PREFIX + name);
            Files.move(dirPath, deletedPath);
        } catch (Exception e){
            LOG.error("Unable to delete dict {}: {}", name, e.toString());
            throw new DataException("Unable to delete dict " + name, e);
        }
    }

    @Override
    public Map<String, Gram> getGramMap(Dict dict) {
        return null;
    }

    @Override
    public void update(Dict dict, Map<String, Gram> gramMap) {

    }
}
