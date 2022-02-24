package ukitinu.markovwords.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import ukitinu.markovwords.lib.Couple;

import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

@Command(name = "list", description = "List the dictionaries")
public class ListCmd extends AbstractCmd {

    @Option(names = {"-d", "--deleted"}, description = "List the deleted dictionaries only")
    boolean listDeleted;

    @Option(names = {"-a", "--all"}, description = "List all dictionaries, deleted included (supersedes --deleted)")
    boolean listAll;

    @Option(names = {"-n", "--name"}, description = "Filter results with the given name, case insensitive")
    String name = "";

    @Override
    public Integer call() {
        try {
            return exec();
        } catch (Exception e) {
            errStream.println(e.getMessage());
            return 1;
        }
    }

    private int exec() {
        var lists = repo.listAll();
        Collection<String> names = getNameList(lists);

        if (names.isEmpty()) {
            errStream.println("No results found");
            return 1;
        }

        names.forEach(outStream::println);
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
