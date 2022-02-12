package ukitinu.markovwords.lib;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

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

    /**
     * Deletes the given directory (if existing).
     * If the directory is not empty, deletes its content too.
     *
     * @param dir dir path.
     * @throws IOException if an error occurs during deletion.
     */
    public static void rmDir(Path dir) throws IOException {
        FileUtils.deleteDirectory(dir.toFile());
    }

    /**
     * Writes the given {@link CharSequence} to the given path, with UTF-8 encoding.
     *
     * @param path    where to write.
     * @param content content to write.
     * @throws IOException if an error occurs whilst writing.
     */
    public static void writeToFile(Path path, CharSequence content) throws IOException {
        Files.writeString(path, content, StandardCharsets.UTF_8);
    }

    /**
     * Reads the content of the file at {@param path}, with UTF-8 encoding.
     *
     * @param path file path.
     * @return file content, as UTF-8
     * @throws IOException if an error occurs whilst reading
     */
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

    /**
     * Moves the {@param src} to {@param dest}, replacing it if already existing (that is, deleting the old one and
     * replacing it with the new).
     *
     * @param src  dir path.
     * @param dest dir path.
     * @throws IOException if an error occurs.
     */
    public static void moveAndReplace(Path src, Path dest) throws IOException {
        rmDir(dest);
        Files.move(src, dest, StandardCopyOption.REPLACE_EXISTING);
    }

}
