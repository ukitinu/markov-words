package ukitinu.markovwords.readers;

@FunctionalInterface
public interface Reader {

    /**
     * Reads the text contained in the source, returning an UTF-8 string.<br>
     * Exceptions are logged and an empty string is returned, so that the process may continue as no-op.
     *
     * @param src source of the text.
     * @return content read from the source.
     */
    String read(String src);
}
