package ukitinu.markovwords;

import ukitinu.markovwords.lib.FsUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

public enum Conf {
    DATA_PATH("data.path", "./.data"),
    GRAM_MAX_LEN("gram.max_length", "3"),
    WRITE_DEPTH("write.depth", "2"),
    WRITE_NUM("write.num", "1"),
    WRITE_MAX_LEN("write.max_length", "100");

    private final String value;

    Conf(String key, String defaultValue) {
        String read = Reader.get(key);
        this.value = read != null ? read : defaultValue;
    }

    public String str() {
        return value;
    }

    public int num() {
        return Integer.parseInt(value);
    }

    private static final class Reader {
        private static final String PROP_FILE = "mkw.properties";
        private static final String MKW_FILE = """
                # Path where the dictionaries' data is stored
                data.path=./.data
                                
                # gram.max_length >= 1
                # Every n-gram directory contains (potentially) a number of file in the order of (alphabet_size)^n
                # For example, an alphabet consisting of a-zA-Z0-9, could potentially have around 250k 3-grams,
                # which is why I heavily suggest not to go above 3, or even lowering it to 2
                gram.max_length=3
                                
                # 1 <= write.depth <= gram.max_length
                # Sets the default depth when using the 'write' command
                write.depth=2
                                
                # write.num >= 1
                # Sets the default number when using the 'write' command
                write.num=1
                                
                # write.max_length >= 1
                # Sets the default max length for a word generated with the 'write' command
                # Non-positive values will cause failure of the 'write' command, values exceeding 512 will be ignored and 512 will be used
                write.max_length=64
                          
                """;

        private static final Properties PROPS = new Properties();

        static {
            try (var is = new FileInputStream(PROP_FILE)) {
                PROPS.load(is);
            } catch (FileNotFoundException e) {
                MarkovWords.ERR.println(PROP_FILE + " not found, generating default one\n");
                try {
                    FsUtils.writeToFile(Path.of(PROP_FILE), MKW_FILE);
                    System.exit(0);
                } catch (IOException ex) {
                    MarkovWords.ERR.println(ex.getMessage());
                    System.exit(1);
                }
            } catch (Exception e) {
                MarkovWords.ERR.println("Unable to read " + PROP_FILE + " file: " + e.getMessage());
                System.exit(1);
            }
        }

        private static String get(String key) {
            return PROPS.getProperty(key);
        }
    }
}
