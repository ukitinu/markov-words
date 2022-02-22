package ukitinu.markovwords;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import ukitinu.markovwords.cmd.*;
import ukitinu.markovwords.repo.FileRepo;
import ukitinu.markovwords.repo.Repo;

import java.io.PrintStream;

@Command(
        name = "mkw",
        subcommands = {
                CreateCmd.class, DeleteCmd.class, InfoCmd.class, ListCmd.class,
                ReadCmd.class, RestoreCmd.class, UpdateCmd.class, WriteCmd.class,
                CommandLine.HelpCommand.class
        },
        description = "Trainable word generator based on Markov Chains",
        mixinStandardHelpOptions = true,
        version = "1.0.0"
)
public class MarkovWords {
    public static final PrintStream OUT = System.out;
    public static final PrintStream ERR = System.err;
    public static final Repo REPO = FileRepo.create(Conf.DATA_PATH.str());

    public static void main(String[] args) {
        int exitCode = new CommandLine(new MarkovWords())
                .setCaseInsensitiveEnumValuesAllowed(true)
                .execute(args);
        System.exit(exitCode);
    }


}
