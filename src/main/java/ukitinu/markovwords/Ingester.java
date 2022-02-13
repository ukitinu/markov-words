package ukitinu.markovwords;

import ukitinu.markovwords.models.Dict;
import ukitinu.markovwords.models.Gram;

import java.util.Map;

public interface Ingester {
    void ingest(String text, Map<String, Gram> gramMap, Dict dict);

    void ingest(String text, Map<String, Gram> gramMap, Dict dict, int len);
}
