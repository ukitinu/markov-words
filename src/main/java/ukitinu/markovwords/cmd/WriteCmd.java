package ukitinu.markovwords.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import ukitinu.markovwords.models.Gram;
import ukitinu.markovwords.repo.DataException;
import ukitinu.markovwords.repo.Repo;

import java.io.PrintStream;
import java.util.Map;
import java.util.concurrent.Callable;

import static ukitinu.markovwords.AlphabetUtils.WORD_END;

@Command(name = "write", description = "Generates words out of the dictionary")
public class WriteCmd implements Callable<Integer> {
    private final Repo repo;
    private final PrintStream printStream;

    public WriteCmd(Repo repo, PrintStream printStream) {
        this.repo = repo;
        this.printStream = printStream;
    }

    @Option(names = {"-d", "--depth"}, description = "Gram depth")
    int depth = 2; //TODO config = MAX-1

    @Option(names = {"--num"}, description = "Number of words to generate")
    int num = 1;

    @Option(names = {"-n", "--name"}, description = "Dictionary name", required = true)
    String name;

    @Override
    public Integer call() {
        if (!repo.exists(name)) {
            printStream.println("Dict not found: " + name);
            return 1;
        }
        try {
            var gramMap = repo.getGramMap(name);
            checkGramMap(gramMap);

            for (int i = 0; i < num; i++) printStream.println(generate(gramMap));
            return 0;
        } catch (DataException e) {
            printStream.println(e.getMessage());
            return 1;
        }
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
        while (next != WORD_END) {
            word.append(next);
            var nextGram = pickNextGram(gram, gramMap, next);
            next = nextGram.next();
        }

        return word.toString();
    }

    private Gram pickNextGram(Gram current, Map<String, Gram> gramMap, char next) {
        if (current.getValue().length() < depth) return gramMap.get(current.getValue() + next);
        return gramMap.get(current.getValue().substring(1) + next);
    }
}
