package ukitinu.markovwords.readers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileReaderIT {
    private final Reader fileReader = new FileReader();

    @Test
    void read() {
        String src = "./src/test/resources/test-text.txt";
        String read = assertDoesNotThrow(() -> fileReader.read(src));
        assertEquals("I just wanted to say that I like Markov chains.\n", read);
    }

    @Test
    void read_error() {
        String src = "./src/test/resources/doesnotexists";
        String read = assertDoesNotThrow(() -> fileReader.read(src));
        assertTrue(read.isEmpty());
    }
}