package ukitinu.markovwords.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import ukitinu.markovwords.AlphabetUtils;
import ukitinu.markovwords.lib.Logger;
import ukitinu.markovwords.models.Dict;
import ukitinu.markovwords.repo.DataException;
import ukitinu.markovwords.repo.Repo;

import java.io.PrintStream;
import java.util.Map;
import java.util.concurrent.Callable;

@Command(name = "create", description = "Create a dictionary")
public class CreateCmd implements Callable<Integer> {
    private static final Logger LOG = Logger.create(CreateCmd.class);

    private final Repo repo;
    private final PrintStream printStream;

    public CreateCmd(Repo repo, PrintStream printStream) {
        this.repo = repo;
        this.printStream = printStream;
    }

    @Option(names = {"-n", "--name"}, description = "Dictionary name", required = true)
    String name;

    @Option(names = {"-d", "--desc"}, description = "Dictionary description", required = true)
    String desc = "";

    @Option(names = {"-a", "--alphabet"}, description = "Dictionary alphabet", required = true)
    String alphabet;

    @Override
    public Integer call() {
        LOG.info("create -- name={} desc={} alphabet={}", name, desc, alphabet);
        if (repo.exists(name)) {
            printStream.println("There is already a dictionary named " + name);
            LOG.warn("create -- ko: there is already a dictionary named {}", name);
            return 1;
        }
        try {
            Dict dict = new Dict(name, desc, AlphabetUtils.convertToSet(alphabet));
            repo.upsert(dict, Map.of());
            printStream.println("New dictionary created: " + name);
            LOG.info("create -- ok");
            return 0;
        } catch (DataException e) {
            printStream.println(e.getMessage());
            LOG.error("create -- ko: {} {}", e.getClass().getSimpleName(), e.getMessage());
            return 1;
        }
    }

}
