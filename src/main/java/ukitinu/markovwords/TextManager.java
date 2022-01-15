package ukitinu.markovwords;

import ukitinu.markovwords.models.Dict;
import ukitinu.markovwords.readers.Reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static ukitinu.markovwords.AlphabetUtils.WORD_END;

public class TextManager {
    private final Reader reader;
    private final Ingester ingester;

    public TextManager(Reader reader, Ingester ingester) {
        this.reader = reader;
        this.ingester = ingester;
    }

    public void processText(String src, Dict dict) {
        String text = reader.read(src);
        String cleaned = cleanText(text, dict);
        ingester.ingest(cleaned, new HashMap<>(), dict, 1);
    }

    /**
     * Cleans the input text according to the following rules:<br>
     * <li>all characters not contained in the dict's alphabet are replaced by WORD_END,</li>
     * <li>consecutive WORD_END chars are squeezed into one,</li>
     * <li>the returned text never ends with a WORD_END,</li>
     * <li>the returned text always starts with a WORD_END,</li>
     * where WORD_END is the char {@link ukitinu.markovwords.AlphabetUtils#WORD_END}.
     *
     * @param text text to clean.
     * @param dict dictionary to use.
     * @return text to ingest.
     */
    String cleanText(String text, Dict dict) {
        List<Character> list = new ArrayList<>();

        for (char letter : text.toCharArray()) {
            if (dict.alphabet().contains(letter)) {
                list.add(letter);
            } else {
                if (!list.isEmpty() && list.get(list.size() - 1) != WORD_END) {
                    list.add(WORD_END);
                }
            }
        }

        if (!list.isEmpty() && list.get(list.size() - 1) == WORD_END) list.remove(list.size() - 1);

        return WORD_END + list.stream().map(String::valueOf).collect(Collectors.joining());
    }
}
