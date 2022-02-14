package ukitinu.markovwords.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import ukitinu.markovwords.lib.Couple;
import ukitinu.markovwords.repo.Repo;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Command(name = "list", aliases = {"ls"}, description = "List the dictionaries")
public class ListCmd implements Callable<Integer> {
    private final Repo repo;
    private final PrintStream printStream;

    public ListCmd(Repo repo, PrintStream printStream) {
        this.repo = repo;
        this.printStream = printStream;
    }

    @Option(names = {"-d", "--deleted"}, description = "List the deleted dictionaries only")
    boolean listDeleted;

    @Option(names = {"-a", "--all"}, description = "List all dictionaries, deleted included")
    boolean listAll;

    @Option(names = {"-n", "--name"}, description = "Filter results with the given name, case insensitive")
    String name = "";

    @Override
    public Integer call() {
        var lists = repo.listAll();
        Collection<String> names = getNameList(lists);

        if (names.isEmpty()) {
            printStream.println("No results found");
            return 1;
        }

        names.forEach(printStream::println);
        return 0;
    }

    private Collection<String> getNameList(Couple<Collection<String>> lists) {
        if (listAll) {
            var visible = filterList(lists.first());
            var deleted = filterList(lists.second());
            visible.addAll(deleted);
            return visible;
        } else if (listDeleted) {
            return filterList(lists.second());
        } else {
            return filterList(lists.first());
        }
    }

    private Collection<String> filterList(Collection<String> list) {
        return list.stream()
                .filter(s -> s.toLowerCase(Locale.ROOT).contains(name.toLowerCase(Locale.ROOT)))
                .sorted()
                // ::toList returns an immutable
                .collect(Collectors.toList());
    }
}
