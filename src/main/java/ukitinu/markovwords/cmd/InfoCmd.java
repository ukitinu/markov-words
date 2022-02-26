package ukitinu.markovwords.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import ukitinu.markovwords.Conf;
import ukitinu.markovwords.models.Dict;
import ukitinu.markovwords.models.Gram;
import ukitinu.markovwords.repo.DataException;
import ukitinu.markovwords.repo.FilePaths;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

@Command(name = "info", description = "Show information about a given dictionary")
public class InfoCmd extends AbstractCmd {

    @Option(names = {"-v", "--verbose"}, description = "Verbose output (shows number of n-grams)")
    boolean verbose;

    @Option(names = {"-vv", "--very-verbose"}, description = "Very verbose output (shows n-grams, supersedes --verbose)")
    boolean veryVerbose;

    @Parameters(paramLabel = "NAME", description = "Dictionary name")
    String name;

    @Override
    public Integer call() {
        try {
            return exec();
        } catch (Exception e) {
            errStream.println(e.getMessage());
            if (e.getMessage().contains("deleted"))
                errStream.println("Use " + FilePaths.DEL_PREFIX + name + " to refer to it");
            return 1;
        }
    }

    private int exec() {
        var dict = repo.get(name);
        printDict(dict);
        return 0;
    }

    private void printDict(Dict dict) {
        outStream.println("name: " + dict.name());
        outStream.println("desc: " + (!dict.desc().isEmpty() ? dict.desc() : ""));
        outStream.println("alphabet: " + toPrintableString(dict.alphabet(), ""));

        if (veryVerbose) {
            for (int len = 1; len <= Conf.GRAM_MAX_LEN.num(); len++) {
                outStream.println(len + "-grams: " + getGramInfo(
                        len,
                        map -> toPrintableString(map.keySet(), " ")
                ));
            }
        } else if (verbose) {
            for (int len = 1; len <= Conf.GRAM_MAX_LEN.num(); len++) {
                outStream.println(len + "-grams: " + getGramInfo(
                        len,
                        map -> String.valueOf(map.size())
                ));
            }
        }
    }

    private String getGramInfo(int len, Function<Map<String, Gram>, String> infoGetter) {
        try {
            return infoGetter.apply(repo.getGramMap(name, len));
        } catch (DataException e) {
            return "";
        }
    }

    private String toPrintableString(Collection<?> collection, String delimiter) {
        return String.join(delimiter, collection
                .stream()
                .map(String::valueOf)
                .sorted()
                .toList());
    }

}
