package ukitinu.markovwords.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import ukitinu.markovwords.Alphabet;
import ukitinu.markovwords.AlphabetUtils;
import ukitinu.markovwords.Validator;
import ukitinu.markovwords.models.Dict;

import java.util.Map;
import java.util.Set;

import static ukitinu.markovwords.AlphabetUtils.WORD_END;

@Command(name = "create", description = "Create a new empty dictionary")
public class CreateCmd extends AbstractCmd {

    @Parameters(paramLabel = "NAME", description = "Dictionary name (English letters, digits and dashes allowed, must start with a letter)")
    String name;

    @Option(names = {"-d", "--desc"}, description = "Dictionary description (English letters, digits, whitespace and punctuation only)")
    String desc = "";

    @Option(names = {"-a", "--alphabet"}, description = "Dictionary alphabet. Use together with --base to add to it, or use it alone. Cannot contain " + WORD_END)
    String alphabet = "";

    @Option(names = {"-b", "--base"}, description = "Base alphabet, empty by default. Valid values: ${COMPLETION-CANDIDATES}")
    Alphabet base = Alphabet.EMPTY;

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
        return 0;
    }

}
