package ukitinu.markovwords.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import ukitinu.markovwords.lib.Logger;
import ukitinu.markovwords.models.Dict;
import ukitinu.markovwords.models.Gram;
import ukitinu.markovwords.repo.DataException;
import ukitinu.markovwords.repo.Repo;

import java.io.PrintStream;
import java.util.Map;
import java.util.concurrent.Callable;

@Command(name = "update", description = "Updates a dictionary")
public class UpdateCmd implements Callable<Integer> {
    private static final Logger LOG = Logger.create(UpdateCmd.class);

    private final Repo repo;
    private final PrintStream printStream;

    public UpdateCmd(Repo repo, PrintStream printStream) {
        this.repo = repo;
        this.printStream = printStream;
    }

    @Option(names = {"-n", "--name"}, description = "Dictionary to update", required = true)
    String name;

    @Option(names = {"-N", "--new-name"}, description = "New name")
    String newName;

    @Option(names = {"-d", "--new-desc"}, description = "New description")
    String newDesc;

    @Override
    public Integer call() {
        if (isMissing(newName) && isMissing(newDesc)) {
            printStream.println("Missing option: at least one of --new-name or --new-desc must be specified");
            return 1;
        }
        if (!isMissing(newName) && repo.exists(newName)) {
            printStream.println("New name " + newName + " is already in use");
            return 1;
        }
        try {
            var currentDict = repo.get(name);
            var newDict = new Dict(
                    isMissing(newName) ? currentDict.name() : newName,
                    isMissing(newDesc) ? currentDict.desc() : newDesc,
                    currentDict.alphabet()
            );
            Map<String, Gram> gramMap = repo.getGramMap(name);

            repo.upsert(newDict, gramMap);

            // if there is no new name then this would delete the updated dict
            if (!isMissing(newName)) repo.delete(name, true);

            printUpdates(currentDict, newDict);
            return 0;
        } catch (DataException e) {
            printStream.println(e.getMessage());
            return 1;
        }
    }

    private boolean isMissing(String opt) {
        return opt == null || opt.isBlank();
    }

    private void printUpdates(Dict oldDict, Dict newDict) {
        printStream.println("Dictionary updated");
        if (!isMissing(newName)) printStream.println("name: " + oldDict.name() + " -> " + newDict.name());
        if (!isMissing(newDesc)) printStream.println("description: " + oldDict.desc() + " -> " + newDict.desc());
    }

}
