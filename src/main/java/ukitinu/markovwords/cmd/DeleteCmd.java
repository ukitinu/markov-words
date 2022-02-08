package ukitinu.markovwords.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import ukitinu.markovwords.repo.DataException;
import ukitinu.markovwords.repo.Repo;

import java.io.PrintStream;
import java.util.concurrent.Callable;

@Command(name = "delete", aliases = {"del"}, description = "Delete the dictionary")
public class DeleteCmd implements Callable<Integer> {
    private final Repo repo;
    private final PrintStream printStream;

    public DeleteCmd(Repo repo, PrintStream printStream) {
        this.repo = repo;
        this.printStream = printStream;
    }

    @Option(names = {"-n", "--name"}, description = "Dictionary name", required = true)
    String name;

    @Override
    public Integer call() {
        try {
            repo.delete(name);
            printStream.println("Dictionary deleted: " + name);
            return 0;
        } catch (DataException e) {
            printStream.println(e.getMessage());
            return 1;
        }
    }

}
