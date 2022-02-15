package ukitinu.markovwords.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import ukitinu.markovwords.lib.Logger;
import ukitinu.markovwords.models.Dict;
import ukitinu.markovwords.repo.DataException;
import ukitinu.markovwords.repo.Repo;

import java.io.PrintStream;
import java.util.Collection;
import java.util.concurrent.Callable;

@Command(name = "info", description = "Shows information about a given dictionary")
public class InfoCmd implements Callable<Integer> {
    private static final Logger LOG = Logger.create(InfoCmd.class);

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
        try {
            var dict = repo.get(name);
            printDict(dict);
            return 0;
        } catch (DataException e) {
            printStream.println(e.getMessage());
            if (e.getMessage().contains("deleted")) printStream.println("Use ." + name + " to refer to it");
            return 1;
        }
    }

    private void printDict(Dict dict) {
        printStream.println(dict.name());
        if (!dict.desc().isEmpty()) printStream.println(dict.desc());

        if (verbose) {
            printStream.println(toPrintableString(dict.alphabet()));
            printStream.println("1-grams: " + getGramKeys(1));
            printStream.println("2-grams: " + getGramKeys(2));
            printStream.println("3-grams: " + getGramKeys(3));
        }
    }

    private String getGramKeys(int len) {
        try {
            var keys = repo.getGramMap(name, len).keySet();
            return toPrintableString(keys);
        } catch (DataException e) {
            return "";
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
