package ukitinu.markovwords.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import ukitinu.markovwords.lib.Logger;
import ukitinu.markovwords.repo.FilePaths;

@Command(name = "delete", description = "Delete a dictionary")
public class DeleteCmd extends AbstractCmd {
    private static final Logger LOG = Logger.create(DeleteCmd.class);
    static final String DEL_PERM_HINT = "refer to it with 'permanent' option on to remove it completely";

    @Option(names = {"-p", "--permanent"}, description = "Permanent deletion")
    boolean permanent;

    @Parameters(paramLabel = "NAME", description = "Dictionary to delete")
    String name;

    @Override
    public Integer call() {
        LOG.info("delete -- name={} permanent={}", name, permanent);
        try {
            return exec();
        } catch (Exception e) {
            errStream.println(e.getMessage());
            if (e.getMessage().contains("deleted state")) {
                errStream.println(DEL_PERM_HINT);
            } else if (e.getMessage().contains("deleted")) {
                errStream.println("Use " + FilePaths.DEL_PREFIX + name + " to " + DEL_PERM_HINT);
            }
            LOG.error("delete -- ko: {} {}", e.getClass().getSimpleName(), e.getMessage());
            return 1;
        }
    }

    private int exec() {
        repo.delete(name, permanent);
        outStream.println(permanent ? "Dictionary deleted permanently: " + name : "Dictionary deleted: " + name);
        LOG.info("delete -- ok");
        return 0;
    }

}
