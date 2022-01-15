package ukitinu.markovwords.readers;

import ukitinu.markovwords.lib.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileReader implements Reader {
    private static final Logger LOG = Logger.create(FileReader.class);

    @Override
    public String read(String src) {
        try {
            var path = Path.of(src);
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOG.error("Unable to read {}: {}", src, e.getMessage());
            return "";
        }
    }
}
