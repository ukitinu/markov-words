package ukitinu.markovwords.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "restore", description = "Restore a deleted dictionary")
public class RestoreCmd extends AbstractCmd {

    @Parameters(paramLabel = "NAME", description = "Deleted dictionary name")
    String name;

    @Override
    public Integer call() {
        try {
            validate();
            return exec();
        } catch (Exception e) {
            errStream.println(e.getMessage());
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
        return 0;
    }

}
