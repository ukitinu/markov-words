package ukitinu.markovwords.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import ukitinu.markovwords.repo.DataException;
import ukitinu.markovwords.repo.Repo;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Callable;

@Command(name = "info", description = "Shows information about a given dictionary")
public class InfoCmd implements Callable<Integer> {
    private final Repo repo;
    private final PrintStream printStream;

    public InfoCmd(Repo repo, PrintStream printStream) {
        this.repo = repo;
        this.printStream = printStream;
    }

    @Option(names = {"-v", "--verbose"}, description = "Verbose output")
    boolean verbose;

    @Option(names = {"-n", "--name"}, description = "Dictionary name", required = true)
    String name;

    @Override
    public Integer call() {
        var dict = repo.get(name);
        var oneGrams = verbose ? getGramKeys(1) : Set.of();
        var twoGrams = verbose ? getGramKeys(2) : Set.of();
        var threeGrams = verbose ? getGramKeys(3) : Set.of();

        String alphabet = toPrintableString(dict.alphabet());
        printStream.println(dict.name());
        printStream.println(alphabet);
        if (verbose) {
            printStream.println("1-grams: " + toPrintableString(oneGrams));
            printStream.println("2-grams: " + toPrintableString(twoGrams));
            printStream.println("3-grams: " + toPrintableString(threeGrams));
        }
        return 0;
    }

    private Set<String> getGramKeys(int len) {
        try {
            return repo.getGramMap(name, len).keySet();
        } catch (DataException e) {
            return Set.of();
        }
    }

    private String toPrintableString(Collection<?> collection) {
        return String.join(" ", collection
                .stream()
                .map(String::valueOf)
                .sorted()
                .toList());
    }

}
