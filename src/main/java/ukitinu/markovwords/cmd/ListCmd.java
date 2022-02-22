package ukitinu.markovwords.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import ukitinu.markovwords.lib.Couple;
import ukitinu.markovwords.lib.Logger;

import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

@Command(name = "list", aliases = {"ls"}, description = "List the dictionaries")
public class ListCmd extends AbstractCmd {
    private static final Logger LOG = Logger.create(ListCmd.class);

    @Option(names = {"-d", "--deleted"}, description = "List the deleted dictionaries only")
    boolean listDeleted;

    @Option(names = {"-a", "--all"}, description = "List all dictionaries, deleted included")
    boolean listAll;

    @Option(names = {"-n", "--name"}, description = "Filter results with the given name, case insensitive")
    String name = "";

    @Override
    public Integer call() {
        LOG.info("list -- name={} all={} deleted={}", name, listAll, listDeleted);
        try {
            return exec();
        } catch (Exception e) {
            errStream.println(e.getMessage());
            LOG.error("list -- ko: {} {}", e.getClass().getSimpleName(), e.getMessage());
            return 1;
        }
    }

    private int exec() {
        var lists = repo.listAll();
        Collection<String> names = getNameList(lists);

        if (names.isEmpty()) {
            errStream.println("No results found");
            LOG.warn("list -- ko: no results");
            return 1;
        }

        names.forEach(outStream::println);
        LOG.info("list -- ok");
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
