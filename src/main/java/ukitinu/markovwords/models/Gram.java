package ukitinu.markovwords.models;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import static ukitinu.markovwords.AlphabetUtils.WORD_END;

@Getter
public class Gram {
    private static final Random RANDOM = new Random();

    private final String value;
    private final Dict dict;
    private final Map<Character, Integer> charMap;
    private int weight;

    public Gram(String value, Dict dict, Map<Character, Integer> charMap) {
        this.value = value;
        this.dict = dict;
        this.charMap = new HashMap<>(charMap);
        this.weight = weigh();
    }

    public Gram(String value, Dict dict) {
        this(value, dict, new HashMap<>());
    }

    public boolean isEmpty() {
        return charMap.isEmpty();
    }

    /**
     * Checks the current weight of the given character
     */
    public int get(Character letter) {
        return charMap.getOrDefault(letter, 0);
    }

    /**
     * Adds 1 to the weight of the character for the Gram and then increases the Gram's total weight.
     * If the char is not in the Gram's alphabet, an IllegalArgumentException in thrown.
     */
    public void increment(Character letter) {
        if (!dict.alphabet().contains(letter)) throw new IllegalArgumentException(letter + " not in alphabet");
        charMap.put(letter, get(letter) + 1);
        weight++;
    }

    /**
     * Picks a character that should follow the Gram, based on weight.
     */
    public Character next() {
        if (weight == 0) this.weight = weigh();
        if (weight == 0) return WORD_END;

        int choice = 1 + RANDOM.nextInt(weight);

        for (var entry : charMap.entrySet()) {
            choice -= entry.getValue();
            if (choice <= 0) return entry.getKey();
        }

        // this should never be reached
        return WORD_END;
    }

    /**
     * "Sums" two Grams with the same alphabet, that is it adds {@param other} weights to this.
     */
    public void add(Gram other) {
        if (!Objects.equals(dict.name(), other.getDict().name())
                || !Objects.equals(dict.alphabet(), other.getDict().alphabet())) {
            throw new IllegalArgumentException("trying to add n-grams from different lexicons");
        }
        if (!value.equals(other.value)) throw new IllegalArgumentException("trying to add different n-grams");

        for (var letter : dict.alphabet()) {
            int count = get(letter) + other.get(letter);
            if (count > 0) charMap.put(letter, count);
        }
    }

    private int weigh() {
        return charMap.values().stream().reduce(0, Integer::sum);
    }

}
