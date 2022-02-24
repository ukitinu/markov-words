package ukitinu.markovwords;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
        private static final Properties PROPS = new Properties();

        static {
            try (var is = new FileInputStream(PROP_FILE)) {
                PROPS.load(is);
            } catch (FileNotFoundException e) {
                MarkovWords.ERR.println(PROP_FILE + " not found");
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
