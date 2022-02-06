package ukitinu.markovwords.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import ukitinu.markovwords.lib.Logger;
import ukitinu.markovwords.repo.Repo;

import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.Callable;

@Command(name = "list", aliases = {"ls"}, description = "List the dictionaries")
public class ListCmd implements Callable<Integer> {
    private static final Logger LOG = Logger.create(ListCmd.class);

    private final Repo repo;

    public ListCmd(Repo repo) {
        this.repo = repo;
    }

    @Option(names = {"-d", "--deleted"}, description = "List the deleted dictionaries only")
    private boolean listDeleted;

    @Option(names = {"-a", "--all"}, description = "List all dictionaries, deleted included")
    private boolean listAll;

    @Option(names = {"-n", "--name"}, description = "Filter results with the given name, case insensitive")
    private String name = "";

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
                .filter(s -> s.toLowerCase(Locale.ROOT).contains(name))
                .sorted()
                .forEach(System.out::println);
    }
}
