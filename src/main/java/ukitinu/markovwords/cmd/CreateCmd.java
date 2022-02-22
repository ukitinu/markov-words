package ukitinu.markovwords.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import ukitinu.markovwords.Alphabet;
import ukitinu.markovwords.AlphabetUtils;
import ukitinu.markovwords.Validator;
import ukitinu.markovwords.lib.Logger;
import ukitinu.markovwords.models.Dict;

import java.util.Map;
import java.util.Set;

@Command(name = "create", description = "Create a dictionary")
public class CreateCmd extends AbstractCmd {
    private static final Logger LOG = Logger.create(CreateCmd.class);

    @Parameters(paramLabel = "NAME", description = "Dictionary name")
    String name;

    @Option(names = {"-d", "--desc"}, description = "Dictionary description")
    String desc = "";

    @Option(names = {"-a", "--alphabet"}, description = "Dictionary alphabet. Use together with --base to add to it, or use it alone")
    String alphabet = "";

    @Option(names = {"-b", "--base"}, description = "Base alphabet, empty by default. Valid values: ${COMPLETION-CANDIDATES}")
    Alphabet base = Alphabet.EMPTY;

    @Override
    public Integer call() {
        LOG.info("create -- name={} desc={} base={} alphabet={}", name, desc, base, alphabet);
        try {
            validate();
            return exec();
        } catch (Exception e) {
            errStream.println(e.getMessage());
            LOG.error("create -- ko: {} {}", e.getClass().getSimpleName(), e.getMessage());
            return 1;
        }
    }

    private void validate() {
        Validator.validateDictName(name);
        Validator.validateDictDesc(desc);
        Validator.validateDictAlphabet(alphabet);

        if (alphabet.isBlank() && base == Alphabet.EMPTY) {
            throw new IllegalArgumentException("missing option: at least one of --alphabet or --base must be specified");
        }

        if (repo.exists(name)) {
            throw new IllegalArgumentException("there is already a dictionary named " + name);
        }
    }

    private int exec() {
        Set<Character> dictChars = base.getChars();
        dictChars.addAll(AlphabetUtils.convertToSet(alphabet));

        Dict dict = new Dict(name, desc, dictChars);
        repo.upsert(dict, Map.of());
        outStream.println("New dictionary created: " + name);
        LOG.info("create -- ok");
        return 0;
    }

}
