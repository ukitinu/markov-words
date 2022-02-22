package ukitinu.markovwords.cmd;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import ukitinu.markovwords.Ingester;
import ukitinu.markovwords.lib.FsUtils;
import ukitinu.markovwords.lib.Logger;
import ukitinu.markovwords.models.Dict;
import ukitinu.markovwords.models.Gram;

import java.nio.file.Path;
import java.util.Map;

@Command(name = "read", description = "Read input text and add it to the dictionary")
public class ReadCmd extends AbstractCmd {
    private static final Logger LOG = Logger.create(ReadCmd.class);

    private final Ingester ingester = new Ingester();

    @ArgGroup(multiplicity = "1")
    ReadInput input;

    static final class ReadInput {
        @Option(names = {"-t", "--text"}, description = "Text", required = true)
        String text;
        @Option(names = {"-f", "--file"}, description = "Path to file", required = true)
        String file;
    }

    @Parameters(paramLabel = "NAME", description = "Dictionary name")
    String name;

    @Override
    public Integer call() {
        LOG.info("read -- name={} text={} file={}", name, input.text, input.file);
        try {
            return exec();
        } catch (Exception e) {
            errStream.println(e.getMessage());
            LOG.error("read -- ko: {} {}", e.getClass().getSimpleName(), e.getMessage());
            return 1;
        }
    }

    private int exec() {
        Dict dict = repo.get(name);
        Map<String, Gram> gramMap = repo.getGramMap(dict.name());

        if (input.text != null) processText(input.text, dict, gramMap, s -> s);
        else processText(input.file, dict, gramMap, s -> FsUtils.readFileSafe(Path.of(s)));

        repo.upsert(dict, gramMap);

        outStream.println("Text read, dictionary " + name + " updated");
        LOG.info("read -- ok");
        return 0;
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
