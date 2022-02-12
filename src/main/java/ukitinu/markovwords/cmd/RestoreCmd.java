package ukitinu.markovwords.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import ukitinu.markovwords.repo.DataException;
import ukitinu.markovwords.repo.Repo;

import java.io.PrintStream;
import java.util.concurrent.Callable;

@Command(name = "restore", description = "Restore a deleted dictionary")
public class RestoreCmd implements Callable<Integer> {
    private final Repo repo;
    private final PrintStream printStream;

    public RestoreCmd(Repo repo, PrintStream printStream) {
        this.repo = repo;
        this.printStream = printStream;
    }

    @Option(names = {"-n", "--name"}, description = "Deleted dictionary name", required = true)
    String name;

    @Override
    public Integer call() {
        try {
            repo.restore(name);
            printStream.println("Dictionary restored: " + name);
            return 0;
        } catch (DataException e) {
            printStream.println(e.getMessage());
            return 1;
        }
    }

}
