package ukitinu.markovwords.cmd;

import ukitinu.markovwords.repo.Repo;

import java.io.PrintStream;
import java.util.concurrent.Callable;

abstract class AbstractCmd implements Callable<Integer> {

    final Repo repo;
    final PrintStream outStream;
    final PrintStream errStream;

    AbstractCmd(Repo repo, PrintStream outStream, PrintStream errStream) {
        this.repo = repo;
        this.outStream = outStream;
        this.errStream = errStream;
    }
}
