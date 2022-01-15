package ukitinu.markovwords;

import ukitinu.markovwords.lib.Pair;
import ukitinu.markovwords.models.Dict;
import ukitinu.markovwords.models.Gram;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DataConverter {
    private static final char NAME_SEP = '\n';
    private static final char GRAM_MAP_SEP = ';';

    /**
     * Converts the dictionary in a string with the dict name on the first line, and the alphabet on the second.
     */
    public String serialiseDict(Dict dict) {
        StringBuilder sb = new StringBuilder();
        sb.append(dict.name()).append(NAME_SEP);
        dict.alphabet().forEach(sb::append);
        return sb.toString();
    }

    /**
     * Converts the gram in a string with the gram value on the first line, and a {@link #GRAM_MAP_SEP}-separated list
     * of {@code CharInt} values.
     */
    public String serialiseGram(Gram gram) {
        StringBuilder sb = new StringBuilder();
        sb.append(gram.getValue()).append(NAME_SEP);
        gram.getCharMap().forEach((k, v) -> sb.append(k).append(v).append(GRAM_MAP_SEP));
        return sb.toString();
    }

    /**
     * Reads the input, everything before {@link #NAME_SEP} is saved into the name, everything after into the alphabet.
     */
    public Dict deserialiseDict(CharSequence cs) {
        var pair = readSlice(cs, 0, NAME_SEP);
        String name = pair.first();

        Set<Character> alphabet = new HashSet<>();
        for (int i = pair.second(); i < cs.length(); i++) {
            alphabet.add(cs.charAt(i));
        }

        return new Dict(name, alphabet);
    }

    /**
     * Creates a new {@link Gram} from the given {@link CharSequence}, with {@param dict} as {@link Dict}.
     */
    public Gram deserialiseGram(CharSequence cs, Dict dict) {
        var gramPair = readGramSerial(cs);

        return new Gram(gramPair.first(), dict, gramPair.second());
    }

    /**
     * Reads the input, everything before {@link #NAME_SEP} is saved into the name, everything after into the char map.
     */
    private Pair<String, Map<Character, Integer>> readGramSerial(CharSequence cs) {
        var namePair = readSlice(cs, 0, NAME_SEP);
        String name = namePair.first();
        int i = namePair.second();

        Map<Character, Integer> charMap = new HashMap<>();
        while (i < cs.length()) {
            char letter = cs.charAt(i);
            var pair = readSlice(cs, i, GRAM_MAP_SEP);
            int count = Integer.parseInt(pair.first());

            charMap.put(letter, count);
            i = pair.second();
        }

        return new Pair<>(name, charMap);
    }

    /**
     * Given a {@link CharSequence}, it reads it from {@param start} until in finds the first occurrence of the char
     * {@param stop}. After this, it returns the string found so far and the index of the first char after the stopchar.
     * <br>
     * The index is incremented in the while loop assignment so that there's no need to increment it again after the
     * loop ends.
     * <br>
     * If the sequence ends before the stopchar, the method returns the string found and, as index, the sequence's length.
     *
     * @param cs    sequence to read.
     * @param start index (inclusive) from which to start.
     * @param stop  character at whose first appearance the read stops.
     * @return pair of string read and index NEXT the stop char.
     */
    private Pair<String, Integer> readSlice(CharSequence cs, int start, char stop) {
        StringBuilder extraction = new StringBuilder();
        int i = start;
        char current;

        while (i < cs.length() && (current = cs.charAt(i++)) != stop) {
            extraction.append(current);
        }
        return new Pair<>(extraction.toString(), i);
    }

}
