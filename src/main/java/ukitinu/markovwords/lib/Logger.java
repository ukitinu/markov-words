package ukitinu.markovwords.lib;

public interface Logger {

    static Logger create(Class<?> clazz) {
        return new Log(clazz);
    }

    void debug(String s);

    void debug(String s, Object o);

    void debug(String s, Object o, Object o1);

    void debug(String s, Object... objects);

    void info(String s);

    void info(String s, Object o);

    void info(String s, Object o, Object o1);

    void info(String s, Object... objects);

    void warn(String s);

    void warn(String s, Object o);

    void warn(String s, Object o, Object o1);

    void warn(String s, Object... objects);

    void error(String s);

    void error(String s, Object o);

    void error(String s, Object o, Object o1);

    void error(String s, Object... objects);
}
