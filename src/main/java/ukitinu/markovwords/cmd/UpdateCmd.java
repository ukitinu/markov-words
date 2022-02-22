package ukitinu.markovwords.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import ukitinu.markovwords.Validator;
import ukitinu.markovwords.lib.Logger;
import ukitinu.markovwords.models.Dict;
import ukitinu.markovwords.models.Gram;

import java.util.Map;

@Command(name = "update", description = "Update a dictionary (name and/or description)")
public class UpdateCmd extends AbstractCmd {
    private static final Logger LOG = Logger.create(UpdateCmd.class);

    @Parameters(paramLabel = "NAME", description = "Dictionary to update")
    String name;

    @Option(names = {"-n", "--new-name"}, description = "New name")
    String newName;

    @Option(names = {"-d", "--new-desc"}, description = "New description")
    String newDesc;

    @Override
    public Integer call() {
        LOG.info("update -- name={} new-name={} new-desc={}", name, newName, newDesc);
        try {
            validate();
            return exec();
        } catch (Exception e) {
            errStream.println(e.getMessage());
            LOG.error("update -- ko: {} {}", e.getClass().getSimpleName(), e.getMessage());
            return 1;
        }
    }

    private void validate() {
        if (!isMissing(newName)) Validator.validateDictName(newName);
        if (!isMissing(newDesc)) Validator.validateDictDesc(newDesc);

        if (isMissing(newName) && isMissing(newDesc)) {
            throw new IllegalArgumentException("missing option: at least one of --new-name or --new-desc must be specified");
        }
        if (!isMissing(newName) && repo.exists(newName)) {
            throw new IllegalArgumentException("new name " + newName + " is already in use");
        }
    }

    private int exec() {
        var currentDict = repo.get(name);
        var newDict = new Dict(
                isMissing(newName) ? currentDict.name() : newName,
                isMissing(newDesc) ? currentDict.desc() : newDesc,
                currentDict.alphabet()
        );
        Map<String, Gram> gramMap = repo.getGramMap(name);

        repo.upsert(newDict, gramMap);

        // if there is no new name then this would delete the updated dict
        if (!isMissing(newName)) {
            LOG.info("update -- removing previous version");
            repo.delete(name, true);
            LOG.info("update -- removed previous version");
        }

        printUpdates(currentDict, newDict);

        LOG.info("update -- ok");
        return 0;
    }

    private boolean isMissing(String opt) {
        return opt == null || opt.isBlank();
    }

    private void printUpdates(Dict oldDict, Dict newDict) {
        outStream.println("Dictionary updated");
        if (!isMissing(newName)) outStream.println("name: " + oldDict.name() + " -> " + newDict.name());
        if (!isMissing(newDesc)) outStream.println("description: " + oldDict.desc() + " -> " + newDict.desc());
    }

}
