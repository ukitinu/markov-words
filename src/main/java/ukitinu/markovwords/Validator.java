package ukitinu.markovwords;

import static ukitinu.markovwords.AlphabetUtils.WORD_END;

public final class Validator {
    static final int DICT_DESC_MAX_LEN = 256;
    private static final int UNICODE_C0 = 32;
    private static final int UNICODE_C1_START = 127;
    private static final int UNICODE_C1_END = 160;

    private Validator() {
        throw new IllegalStateException("non-instantiable");
    }

    public static void validateDictName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("dict name must not be empty");
        if (!name.matches("^[a-zA-Z][a-zA-Z0-9\\-]*$")) {
            throw new IllegalArgumentException("dict name must consist of english letters, digits and dashes and must start with a letter");
        }
    }

    public static void validateDictDesc(String desc) {
        if (desc == null) throw new IllegalArgumentException("dict desc must not be null");
        if (desc.length() > DICT_DESC_MAX_LEN) {
            throw new IllegalArgumentException("dict desc max length is " + DICT_DESC_MAX_LEN + " characters (current is " + desc.length() + ")");
        }
        if (!desc.matches("^[\\p{Alnum}\\p{Punct} ]*$")) {
            throw new IllegalArgumentException("dict desc must consist of english letters, digits, whitespace and punctuation only");
        }
    }

    public static void validateDictAlphabet(String alphabet) {
        if (alphabet == null) throw new IllegalArgumentException("dict alphabet must not be null");
        StringBuilder err = new StringBuilder();
        char[] chars = alphabet.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c <= UNICODE_C0 || c == WORD_END || (c >= UNICODE_C1_START && c <= UNICODE_C1_END)) {
                err.append("\t") // NOPMD
                        .append("invalid unicode at position ")
                        .append(i)
                        .append(" with hex value ")
                        .append(Integer.toHexString(c))
                        .append(c == WORD_END ? " (reserved character)" : " (control characters not allowed)")
                        .append(System.lineSeparator());
            }
        }

        // strip() removes the last line separator
        if (!err.isEmpty()) {
            throw new IllegalArgumentException("invalid alphabet:" + System.lineSeparator() + err.toString().strip());
        }
    }
}
