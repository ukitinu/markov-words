package ukitinu.markovwords.models;

import java.util.HashSet;
import java.util.Set;

import static ukitinu.markovwords.AlphabetUtils.WORD_END;


public record Dict(String name, Set<Character> alphabet) {
    public Dict(String name, Set<Character> alphabet) {
        this.name = name;
        this.alphabet = new HashSet<>(alphabet);
        this.alphabet.add(WORD_END);
    }
}
