package ukitinu.markovwords.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import ukitinu.markovwords.models.Dict;
import ukitinu.markovwords.models.Gram;
import ukitinu.markovwords.repo.DataException;
import ukitinu.markovwords.repo.Repo;

import java.io.PrintStream;
import java.util.Map;
import java.util.concurrent.Callable;

@Command(name = "rename", description = "Renames a dictionary")
public class RenameCmd implements Callable<Integer> {
    private final Repo repo;
    private final PrintStream printStream;

    public RenameCmd(Repo repo, PrintStream printStream) {
        this.repo = repo;
        this.printStream = printStream;
    }

    @Option(names = {"-n", "--name"}, description = "Dictionary to rename", required = true)
    String name;

    @Option(names = {"--new-name"}, description = "New name", required = true)
    String newName;

    @Override
    public Integer call() {
        if (repo.exists(newName)) {
            printStream.println("New name " + newName + " is already in use");
            return 1;
        }
        try {
            var currentDict = repo.get(name);
            var newDict = new Dict(newName, currentDict.alphabet());
            Map<String, Gram> gramMap = repo.getGramMap(name);

            repo.upsert(newDict, gramMap);
            repo.delete(name, true);

            printStream.println("Dictionary renamed from " + name + " to " + newName);
            return 0;
        } catch (DataException e) {
            printStream.println(e.getMessage());
            return 1;
        }
    }

}
