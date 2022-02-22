package ukitinu.markovwords.cmd;

import ukitinu.markovwords.MarkovWords;
import ukitinu.markovwords.repo.Repo;

import java.io.PrintStream;
import java.util.concurrent.Callable;

abstract class AbstractCmd implements Callable<Integer> {

    protected Repo repo = MarkovWords.REPO;
    protected PrintStream outStream = MarkovWords.OUT;
    protected PrintStream errStream = MarkovWords.ERR;

    /**
     * Use this instead of constructor because picocli prefers to have nullary constructors for the subcommand classes.
     */
    public void redirect(Repo repo, PrintStream outStream, PrintStream errStream) {
        this.repo = repo;
        this.outStream = outStream;
        this.errStream = errStream;
    }
}
