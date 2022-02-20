package ukitinu.markovwords.conf;

import ukitinu.markovwords.lib.Logger;

import java.io.FileInputStream;
import java.util.Properties;

public enum Property {
    DATA_PATH("data.path", "./.data"),
    GRAM_MAX_LEN("gram.max_length", "3"),
    WRITE_DEPTH("write.depth", "2"),
    WRITE_NUM("write.num", "1");

    private final String value;

    Property(String key, String defaultValue) {
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
        private static final Logger LOG = Logger.create(Reader.class);
        private static final String PROP_FILE = "markov-words.properties";
        private static final Properties PROPS = new Properties();

        static {
            try (var is = new FileInputStream(PROP_FILE)) {
                PROPS.load(is);
            } catch (Exception e) {
                LOG.error("Unable to read {} file: {}", PROP_FILE, e.getMessage());
                System.exit(1);
            }
        }

        private static String get(String key) {
            return PROPS.getProperty(key);
        }
    }
}
