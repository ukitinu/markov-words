package ukitinu.markovwords.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import ukitinu.markovwords.repo.Repo;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.Callable;

@Command(name = "list", aliases = {"ls"}, description = "List the dictionaries")
public class ListCmd implements Callable<Integer> {
    private final Repo repo;
    private final PrintStream printStream;

    public ListCmd(Repo repo, PrintStream printStream) {
        this.repo = repo;
        this.printStream = printStream;
    }

    /* OPTIONS */

    @Option(names = {"-d", "--deleted"}, description = "List the deleted dictionaries only")
    boolean listDeleted;

    @Option(names = {"-a", "--all"}, description = "List all dictionaries, deleted included")
    boolean listAll;

    @Option(names = {"-n", "--name"}, description = "Filter results with the given name, case insensitive")
    String name = "";

    /* COMMAND */

    @Override
    public Integer call() {
        var lists = repo.listAll();
        if (listAll) {
            printNames(lists.first());
            printNames(lists.second());
        } else if (listDeleted) {
            printNames(lists.second());
        } else {
            printNames(lists.first());
        }
        return 0;
    }

    private void printNames(Collection<String> list) {
        list.stream()
                .filter(s -> s.toLowerCase(Locale.ROOT).contains(name.toLowerCase(Locale.ROOT)))
                .sorted()
                .forEach(printStream::println);
    }
}