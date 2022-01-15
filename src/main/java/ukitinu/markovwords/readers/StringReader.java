package ukitinu.markovwords.readers;

public class StringReader implements Reader {
    @Override
    public String read(String src) {
        //TODO check charset handling from command line
        return src;
    }
}
