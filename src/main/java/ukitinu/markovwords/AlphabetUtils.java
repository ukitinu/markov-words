package ukitinu.markovwords;

import ukitinu.markovwords.models.Dict;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class AlphabetUtils {
    private static final char ASCII_A_UPPER = 65;
    private static final char ASCII_Z_UPPER = 90;
    private static final char ASCII_A_LOWER = 97;
    private static final char ASCII_Z_LOWER = 122;
    private static final char ASCII_0 = 48;
    private static final char ASCII_9 = 57;

    private AlphabetUtils() {
        throw new IllegalStateException("non-instantiable");
    }

    public static final char WORD_END = '_';

    public static Set<Character> convertToSet(String string) {
        Set<Character> set = new HashSet<>();
        for (char c : string.toCharArray()) {
            set.add(c);
        }
        return set;
    }

    /**
     * @return Set of letters A-Z, a-z, 0-9
     */
    public static Set<Character> getAsciiSimple() {
        Set<Character> set = new HashSet<>();
        set.addAll(getAsciiLetters());
        set.addAll(getAsciiDigits());
        return set;
    }

    /**
     * @return Set of letters A-Z, a-z
     */
    public static Set<Character> getAsciiLetters() {
        Set<Character> set = new HashSet<>();
        set.addAll(getAsciiLettersUpper());
        set.addAll(getAsciiLettersLower());
        return set;
    }

    /**
     * @return Set of letters A-Z
     */
    public static Set<Character> getAsciiLettersUpper() {
        return getAsciiRange(ASCII_A_UPPER, ASCII_Z_UPPER);
    }

    /**
     * @return Set of letters a-z
     */
    public static Set<Character> getAsciiLettersLower() {
        return getAsciiRange(ASCII_A_LOWER, ASCII_Z_LOWER);
    }

    /**
     * @return Set of letters 0-9
     */
    public static Set<Character> getAsciiDigits() {
        return getAsciiRange(ASCII_0, ASCII_9);
    }

    /**
     * Cleans the input text according to the following rules:<br>
     * <li>all characters not contained in the dict's alphabet are replaced by WORD_END,</li>
     * <li>consecutive WORD_END chars are squeezed into one,</li>
     * <li>the returned text always ends with a WORD_END,</li>
     * <li>the returned text always starts with a WORD_END,</li>
     * where WORD_END is the char {@link ukitinu.markovwords.AlphabetUtils#WORD_END}.
     *
     * @param text text to clean.
     * @param dict dictionary to use.
     * @return text to ingest.
     */
    public static String cleanText(String text, Dict dict) {
        List<Character> list = new ArrayList<>();

        list.add(WORD_END);
        for (char letter : text.toCharArray()) {
            if (dict.alphabet().contains(letter) && letter != WORD_END) {
                list.add(letter);
            } else {
                if (list.get(list.size() - 1) != WORD_END) {
                    list.add(WORD_END);
                }
            }
        }

        if (list.get(list.size() - 1) != WORD_END) list.add(WORD_END);

        return list.stream().map(String::valueOf).collect(Collectors.joining());
    }

    /**
     * Creates a set of chars with the given extremes (inclusive).
     *
     * @param start starting character (inclusive)
     * @param end   ending character (inclusive)
     * @return set of characters in the given range
     */
    private static Set<Character> getAsciiRange(char start, char end) {
        Set<Character> set = new HashSet<>();
        for (char c = start; c <= end; c++) {
            set.add(c);
        }
        return set;
    }
}
