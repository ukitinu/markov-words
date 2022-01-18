package ukitinu.markovwords.repo;

import ukitinu.markovwords.models.Dict;
import ukitinu.markovwords.models.Gram;

import java.util.Collection;
import java.util.Map;

public interface Repo {

    Collection<String> listAll();

    Dict get(String name);

    Gram getGram(String dictName, String gramValue);

    void delete(String name);

    Map<String, Gram> getGramMap(String name, int len);

    void update(Dict dict, Map<String, Gram> gramMap);

}
