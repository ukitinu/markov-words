package ukitinu.markovwords;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

public enum Alphabet {
    ALPHANUM(AlphabetUtils.getAsciiSimple()),
    LETTERS(AlphabetUtils.getAsciiLetters()),
    UPPER(AlphabetUtils.getAsciiLettersUpper()),
    LOWER(AlphabetUtils.getAsciiLettersLower()),
    DIGITS(AlphabetUtils.getAsciiDigits()),
    EMPTY(new HashSet<>());

    @Getter
    private final Set<Character> chars;

    Alphabet(Set<Character> chars) {
        this.chars = chars;
    }

}
