package ukitinu.markovwords.lib;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FsUtilsTest {

    @Test
    void cpDir() throws IOException {
        final Path testDirSource = Path.of("./src/test/resources/cp-dir");
        final Path testDirDest = Path.of("./src/test/resources/cp-dir-dest");
        try {
            assertDoesNotThrow(() -> FsUtils.cpDir(testDirSource, testDirDest));

            assertTrue(Files.exists(testDirDest));
            assertTrue(Files.isDirectory(testDirDest));

            List<Path> firstDepth = Files.list(testDirDest).toList();
            assertEquals(3, firstDepth.size());
            assertTrue(firstDepth.contains(Path.of(testDirDest + "/s0")));
            assertTrue(firstDepth.contains(Path.of(testDirDest + "/s1")));

            assertEquals(
                    List.of(Path.of(testDirDest + "/s1/s10")),
                    Files.list(Path.of(testDirDest + "/s1")).toList()
            );
            assertEquals(
                    List.of(Path.of(testDirDest + "/s1/s10/s10_f0.txt")),
                    Files.list(Path.of(testDirDest + "/s1/s10")).toList()
            );


            var s0Expected = List.of(Path.of(testDirDest + "/s0/s00"), Path.of(testDirDest + "/s0/s0_f0.txt"));
            var s0Found = Files.list(Path.of(testDirDest + "/s0")).toList();
            assertTrue(s0Expected.size() == s0Found.size() && s0Expected.containsAll(s0Found) && s0Found.containsAll(s0Expected));

            assertEquals(
                    List.of(Path.of(testDirDest + "/s0/s00"), Path.of(testDirDest + "/s0/s0_f0.txt")),
                    Files.list(Path.of(testDirDest + "/s0")).toList()
            );
            assertEquals(
                    List.of(Path.of(testDirDest + "/s0/s00/s00_f0.txt")),
                    Files.list(Path.of(testDirDest + "/s0/s00")).toList()
            );
        } finally {
            FileUtils.deleteDirectory(testDirDest.toFile());
        }
    }
}