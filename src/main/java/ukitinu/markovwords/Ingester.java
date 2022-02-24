package ukitinu.markovwords;

import ukitinu.markovwords.models.Dict;
import ukitinu.markovwords.models.Gram;

import java.util.Map;

import static ukitinu.markovwords.AlphabetUtils.WORD_END;

public class Ingester {

    public void ingest(String text, Map<String, Gram> gramMap, Dict dict) {
        if (Conf.GRAM_MAX_LEN.num() <= 0) {
            throw new IllegalArgumentException("'gram.max_length' property must be positive");
        }
        for (int len = 1; len <= Conf.GRAM_MAX_LEN.num(); len++) {
            ingest(text, gramMap, dict, len);
        }
    }

    public void ingest(String text, Map<String, Gram> gramMap, Dict dict, int len) {
        if (text == null || gramMap == null || dict == null) {
            throw new IllegalArgumentException("Parameters must not be null");
        }
        if (len <= 0) throw new IllegalArgumentException("len must be positive");

        String cleaned = AlphabetUtils.cleanText(text, dict);
        if (len > cleaned.length()) return;

        Gram current = getGram(cleaned.substring(0, len), gramMap, dict);
        char[] chars = cleaned.toCharArray();

        for (int i = len; i < chars.length; i++) {
            char letter = chars[i];
            current.increment(letter);
            String nextKey = current.getValue().substring(1) + letter;
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
