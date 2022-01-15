package ukitinu.markovwords;

import ukitinu.markovwords.models.Dict;
import ukitinu.markovwords.models.Gram;

import java.util.Map;

import static ukitinu.markovwords.AlphabetUtils.WORD_END;

public class IngesterImpl implements Ingester {
    @Override
    public void ingest(String text, Map<String, Gram> gramMap, Dict dict, int len) {
        if (text == null || gramMap == null || dict == null) {
            throw new IllegalArgumentException("Parameters must not be null");
        }
        if (len <= 0) throw new IllegalArgumentException("len must be positive");

        if (len > text.length()) return;

        Gram current = getGram(text.substring(0, len), gramMap, dict);
        char[] chars = text.toCharArray();

        for (int i = len; i < chars.length; i++) {
            char letter = chars[i];
            current.increment(letter);
            String nextKey = current.getGram().substring(1) + letter;
            current = getGram(nextKey, gramMap, dict);
        }
        current.increment(WORD_END);
    }

    private Gram getGram(String text, Map<String, Gram> gramMap, Dict dict) {
        if (!gramMap.containsKey(text)) {
            gramMap.put(text, new Gram(text, dict));
        }
        return gramMap.get(text);
    }
}
