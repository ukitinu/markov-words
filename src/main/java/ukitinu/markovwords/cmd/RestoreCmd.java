package ukitinu.markovwords.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import ukitinu.markovwords.lib.Logger;

@Command(name = "restore", description = "Restore a deleted dictionary")
public class RestoreCmd extends AbstractCmd {
    private static final Logger LOG = Logger.create(RestoreCmd.class);

    @Option(names = {"-n", "--name"}, description = "Deleted dictionary name", required = true)
    String name;

    @Override
    public Integer call() {
        LOG.info("restore -- name={}", name);
        try {
            validate();
            return exec();
        } catch (Exception e) {
            errStream.println(e.getMessage());
            LOG.error("restore -- ko: {} {}", e.getClass().getSimpleName(), e.getMessage());
            return 1;
        }
    }

    private void validate() {
        if (!repo.exists(name)) {
            throw new IllegalArgumentException("given dictionary does not exists: " + name);
        }
    }

    private int exec() {
        repo.restore(name);
        outStream.println("Dictionary restored: " + name);
        LOG.info("restore -- ok");
        return 0;
    }

}
