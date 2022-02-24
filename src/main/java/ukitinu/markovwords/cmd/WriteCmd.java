package ukitinu.markovwords.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import ukitinu.markovwords.Conf;
import ukitinu.markovwords.models.Gram;
import ukitinu.markovwords.repo.DataException;

import java.util.Map;

import static ukitinu.markovwords.AlphabetUtils.WORD_END;

@Command(name = "write", description = "Generate words out of the dictionary")
public class WriteCmd extends AbstractCmd {
    private static final int LEN_ROOF = 513; // 512 + 1

    @Option(names = {"-d", "--depth"}, description = "Gram depth (default in write.depth in properties file)")
    int depth = Conf.WRITE_DEPTH.num();

    @Option(names = {"-n", "--num"}, description = "Number of words to generate (default in write.num in properties file)")
    int num = Conf.WRITE_NUM.num();

    @Option(names = {"-m", "--max-len"}, description = "Max length of a generated word (default in write.max_length in properties file)")
    int maxLen = Conf.WRITE_MAX_LEN.num();

    @Parameters(paramLabel = "NAME", description = "Dictionary name")
    String name;

    @Override
    public Integer call() {
        try {
            validate();
            return exec();
        } catch (Exception e) {
            errStream.println(e.getMessage());
            return 1;
        }
    }

    private void validate() {
        if (!repo.exists(name)) {
            throw new IllegalArgumentException("dict not found: " + name);
        }
        if (maxLen <= 0) {
            throw new IllegalArgumentException("max-len value must be positive");
        }
    }

    private int exec() {
        var gramMap = repo.getGramMap(name);
        checkGramMap(gramMap);

        for (int i = 0; i < num; i++) outStream.println(generate(gramMap));

        return 0;
    }

    private void checkGramMap(Map<String, Gram> gramMap) {
        if (gramMap.isEmpty()) throw new DataException("No grams in the dictionary");
        if (!gramMap.containsKey(String.valueOf(WORD_END))) throw new DataException("Missing WORD_END gram");
        for (int len = 1; len <= depth; len++) {
            if (!repo.hasGramMap(name, len)) throw new DataException("Missing " + len + "-grams");
        }
    }

    private String generate(Map<String, Gram> gramMap) {
        var word = new StringBuilder();
        Gram gram = gramMap.get(String.valueOf(WORD_END));

        char next = gram.next();
        while (next != WORD_END && word.length() < maxLen && word.length() < LEN_ROOF) {
            word.append(next);
            var nextGram = pickNextGram(gram, gramMap, next);
            next = nextGram.next();
        }

        return word.toString();
    }

    /**
     * The logic is that if the next gram should either be the previous plus the current character if its length is
     * below {@link #depth}, or the previous, with its first char dropped, plus the current.<br>
     * If the required gram is not found in the dictionary, it falls back to the 1-gram of {@param next}.
     */
    private Gram pickNextGram(Gram current, Map<String, Gram> gramMap, char next) {
        var simpleNext = gramMap.get(String.valueOf(next));
        if (current.getValue().length() < depth) {
            return gramMap.getOrDefault(current.getValue() + next, simpleNext);
        }
        return gramMap.getOrDefault(current.getValue().substring(1) + next, simpleNext);
    }
}
