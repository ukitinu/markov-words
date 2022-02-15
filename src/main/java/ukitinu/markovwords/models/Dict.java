package ukitinu.markovwords.models;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static ukitinu.markovwords.AlphabetUtils.WORD_END;


public final class Dict {
    private final String name;
    private final String desc;
    private final Set<Character> alphabet;

    public Dict(String name, String desc, Set<Character> alphabet) {
        this.name = name;
        this.desc = desc;
        this.alphabet = new HashSet<>(alphabet);
        this.alphabet.add(WORD_END);
    }

    public Dict(String name, Set<Character> alphabet) {
        this(name, "", alphabet);
    }

    public String name() {
        return name;
    }

    public String desc() {
        return desc;
    }

    public Set<Character> alphabet() {
        return alphabet;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Dict) obj;
        return Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Dict " + name;
    }

}
