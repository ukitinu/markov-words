package ukitinu.markovwords.readers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileReader implements Reader {
    @Override
    public String read(String src) {
        try {
            var path = Path.of(src);
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }
}
