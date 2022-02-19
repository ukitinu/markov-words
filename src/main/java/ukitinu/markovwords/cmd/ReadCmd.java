package ukitinu.markovwords.cmd;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import ukitinu.markovwords.Ingester;
import ukitinu.markovwords.lib.FsUtils;
import ukitinu.markovwords.lib.Logger;
import ukitinu.markovwords.models.Dict;
import ukitinu.markovwords.models.Gram;
import ukitinu.markovwords.repo.DataException;
import ukitinu.markovwords.repo.Repo;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.Callable;

@Command(name = "read", description = "Read input text and add it to the dictionary")
public class ReadCmd implements Callable<Integer> {
    private static final Logger LOG = Logger.create(ReadCmd.class);

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
        LOG.info("read -- name={} text={} file={}", name, input.text, input.file);
        try {
            Dict dict = repo.get(name);
            Map<String, Gram> gramMap = repo.getGramMap(dict.name());

            if (input.text != null) processText(input.text, dict, gramMap, s -> s);
            else processText(input.file, dict, gramMap, s -> FsUtils.readFileSafe(Path.of(s)));

            repo.upsert(dict, gramMap);

            printStream.println("Text read, dictionary " + name + " updated");
            LOG.info("read -- ok");
            return 0;
        } catch (DataException e) {
            printStream.println(e.getMessage());
            LOG.error("read -- ko: {} {}", e.getClass().getSimpleName(), e.getMessage());
            return 1;
        }
    }

    private void processText(String src, Dict dict, Map<String, Gram> gramMap, Reader reader) {
        String text = reader.read(src);
        ingester.ingest(text, gramMap, dict);
    }

    @FunctionalInterface
    private interface Reader {
        /**
         * Reads the text contained in the source, returning an UTF-8 string.<br>
         * Exceptions are logged and an empty string is returned, so that the process may continue as no-op.
         *
         * @param src source of the text.
         * @return content read from the source.
         */
        String read(String src);
    }

}
