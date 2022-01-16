package ukitinu.markovwords.repo;

import ukitinu.markovwords.lib.Logger;
import ukitinu.markovwords.models.Dict;
import ukitinu.markovwords.models.Gram;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class FileRepo implements Repo {
    private static final Logger LOG = Logger.create(FileRepo.class);

    private final String baseDir;

    public FileRepo(String baseDir) {
        this.baseDir = baseDir;
    }

    @Override
    public Collection<String> listAll() {
        try (var stream = Files.list(Path.of(baseDir))) {
            return stream
                    .filter(Files::isDirectory)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            LOG.error("Unable to read {} dir: {}", baseDir, e.toString());
            return Collections.emptyList();
        }
    }

    @Override
    public Dict get(String name) {
        return null;
    }

    @Override
    public Dict delete(String name) {
        return null;
    }

    @Override
    public Map<String, Gram> getGramMap(Dict dict) {
        return null;
    }

    @Override
    public void update(Dict dict, Map<String, Gram> gramMap) {

    }
}
