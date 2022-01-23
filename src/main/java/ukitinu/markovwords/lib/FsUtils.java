package ukitinu.markovwords.lib;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FsUtils {
    private FsUtils() {
        throw new IllegalStateException("non-instantiable");
    }

    /**
     * If the directory does not exists, it creates it, with all its parents if necessary.
     *
     * @param dir dir path.
     * @throws IOException if an error occurs during creation.
     */
    public static void mkDir(Path dir) throws IOException {
        if (Files.notExists(dir)) Files.createDirectories(dir);
    }

    public static void writeToFile(Path path, String content) throws IOException {
        Files.writeString(path, content, StandardCharsets.UTF_8);
    }

    public static String readFile(Path path) throws IOException {
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    /**
     * If the directory {@param src} does not exists, it creates it, otherwise it copies it with all its contents
     * to the given destination {@param dest}.
     *
     * @param src  path of the directory to copy.
     * @param dest path of the copy to create.
     * @throws IOException if it fails during the copy.
     */
    public static void cpDir(Path src, Path dest) throws IOException {
        if (Files.exists(src)) {
            var subDirs = Files.walk(src).toList();
            for (var sub : subDirs) {
                String subRelativePath = sub.toString().substring(src.toString().length());
                Path destination = Path.of(dest.toString(), subRelativePath);
                Files.copy(sub, destination);
            }
        } else {
            mkDir(dest);
        }
    }


}
