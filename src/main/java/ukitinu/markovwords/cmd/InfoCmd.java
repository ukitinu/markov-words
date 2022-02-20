package ukitinu.markovwords.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import ukitinu.markovwords.lib.Logger;
import ukitinu.markovwords.models.Dict;
import ukitinu.markovwords.repo.DataException;
import ukitinu.markovwords.repo.Repo;

import java.io.PrintStream;
import java.util.Collection;

@Command(name = "info", description = "Shows information about a given dictionary")
public class InfoCmd extends AbstractCmd {
    private static final Logger LOG = Logger.create(InfoCmd.class);

    public InfoCmd(Repo repo, PrintStream outStream, PrintStream errStream) {
        super(repo, outStream, errStream);
    }

    @Option(names = {"-v", "--verbose"}, description = "Verbose output")
    boolean verbose;

    @Option(names = {"-n", "--name"}, description = "Dictionary name", required = true)
    String name;

    @Override
    public Integer call() {
        LOG.info("info -- name={} verbose={}", name, verbose);
        try {
            return exec();
        } catch (Exception e) {
            errStream.println(e.getMessage());
            if (e.getMessage().contains("deleted")) errStream.println("Use ." + name + " to refer to it");
            LOG.error("info -- ko: {} {}", e.getClass().getSimpleName(), e.getMessage());
            return 1;
        }
    }

    private int exec() {
        var dict = repo.get(name);
        printDict(dict);
        LOG.info("info -- ok");
        return 0;
    }

    private void printDict(Dict dict) {
        outStream.println(dict.name());
        if (!dict.desc().isEmpty()) outStream.println(dict.desc());

        if (verbose) {
            outStream.println(toPrintableString(dict.alphabet()));
            outStream.println("1-grams: " + getGramKeys(1));
            outStream.println("2-grams: " + getGramKeys(2));
            outStream.println("3-grams: " + getGramKeys(3));
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
