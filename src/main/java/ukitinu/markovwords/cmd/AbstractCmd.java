package ukitinu.markovwords.cmd;

import ukitinu.markovwords.repo.Repo;

import java.io.PrintStream;
import java.util.concurrent.Callable;

abstract class AbstractCmd implements Callable<Integer> {

    protected Repo repo;
    protected PrintStream outStream;
    protected PrintStream errStream;

    /**
     * Use this instead of constructor because picocli prefers to have nullary constructors for the subcommand classes.
     */
    public void init(Repo repo, PrintStream outStream, PrintStream errStream) {
        this.repo = repo;
        this.outStream = outStream;
        this.errStream = errStream;
    }
}
