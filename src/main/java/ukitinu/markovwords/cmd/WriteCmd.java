package ukitinu.markovwords.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import ukitinu.markovwords.conf.Property;
import ukitinu.markovwords.lib.Logger;
import ukitinu.markovwords.models.Gram;
import ukitinu.markovwords.repo.DataException;
import ukitinu.markovwords.repo.Repo;

import java.io.PrintStream;
import java.util.Map;

import static ukitinu.markovwords.AlphabetUtils.WORD_END;

@Command(name = "write", description = "Generates words out of the dictionary")
public class WriteCmd extends AbstractCmd {
    private static final Logger LOG = Logger.create(WriteCmd.class);

    public WriteCmd(Repo repo, PrintStream outStream, PrintStream errStream) {
        super(repo, outStream, errStream);
    }

    @Option(names = {"-d", "--depth"}, description = "Gram depth")
    int depth = Property.WRITE_DEPTH.num();

    @Option(names = {"--num"}, description = "Number of words to generate")
    int num = Property.WRITE_NUM.num();

    @Option(names = {"-n", "--name"}, description = "Dictionary name", required = true)
    String name;

    @Override
    public Integer call() {
        LOG.info("write -- name={} num={} depth={}", name, num, depth);
        try {
            validate();
            return exec();
        } catch (Exception e) {
            errStream.println(e.getMessage());
            LOG.error("write -- ko: {} {}", e.getClass().getSimpleName(), e.getMessage());
            return 1;
        }
    }

    private void validate() {
        if (!repo.exists(name)) {
            throw new IllegalArgumentException("dict not found: " + name);
        }
    }

    private int exec() {
        var gramMap = repo.getGramMap(name);
        checkGramMap(gramMap);

        for (int i = 0; i < num; i++) outStream.println(generate(gramMap));

        LOG.info("write -- ok");
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
