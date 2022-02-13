package ukitinu.markovwords.cmd;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import ukitinu.markovwords.Ingester;
import ukitinu.markovwords.models.Dict;
import ukitinu.markovwords.models.Gram;
import ukitinu.markovwords.readers.FileReader;
import ukitinu.markovwords.readers.Reader;
import ukitinu.markovwords.readers.StringReader;
import ukitinu.markovwords.repo.DataException;
import ukitinu.markovwords.repo.Repo;

import java.io.PrintStream;
import java.util.Map;
import java.util.concurrent.Callable;

@Command(name = "read", description = "Read input text and add it to the dictionary")
public class ReadCmd implements Callable<Integer> {
    private final Repo repo;
    private final PrintStream printStream;
    private final Ingester ingester;

    public ReadCmd(Repo repo, PrintStream printStream, Ingester ingester) {
        this.repo = repo;
        this.printStream = printStream;
        this.ingester = ingester;
    }

    @ArgGroup(multiplicity = "1")
    ReadInput input;

    static final class ReadInput {
        @Option(names = {"-t", "--text"}, description = "Text", required = true)
        String text;
        @Option(names = {"-f", "--file"}, description = "Path to file", required = true)
        String file;
    }

    @Option(names = {"-n", "--name"}, description = "Dictionary name", required = true)
    String name;

    @Override
    public Integer call() {
        try {
            Dict dict = repo.get(name);
            Map<String, Gram> gramMap = repo.getGramMap(dict.name());

            if (input.text != null) processText(input.text, dict, gramMap, new StringReader());
            else processText(input.file, dict, gramMap, new FileReader());

            repo.upsert(dict, gramMap);

            printStream.println("Text read, dictionary " + name + " updated");
            return 0;
        } catch (DataException e) {
            printStream.println(e.getMessage());
            return 1;
        }
    }

    private void processText(String src, Dict dict, Map<String, Gram> gramMap, Reader reader) {
        String text = reader.read(src);
        ingester.ingest(text, gramMap, dict);
    }

}
