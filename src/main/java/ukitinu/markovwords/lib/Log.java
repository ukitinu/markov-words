package ukitinu.markovwords.lib;

import org.apache.logging.log4j.LogManager;

class Log implements Logger {
    private final org.apache.logging.log4j.Logger log;

    Log(Class<?> clazz) {
        this.log = LogManager.getLogger(clazz);
    }

    @Override
    public void debug(String s) {
        log.debug(s);
    }

    @Override
    public void debug(String s, Object o) {
        log.debug(s, o);
    }

    @Override
    public void debug(String s, Object o, Object o1) {
        log.debug(s, o, o1);
    }

    @Override
    public void debug(String s, Object... objects) {
        log.debug(s, objects);
    }

    @Override
    public void info(String s) {
        log.info(s);
    }

    @Override
    public void info(String s, Object o) {
        log.info(s, o);
    }

    @Override
    public void info(String s, Object o, Object o1) {
        log.info(s, o, o1);
    }

    @Override
    public void info(String s, Object... objects) {
        log.info(s, objects);
    }

    @Override
    public void warn(String s) {
        log.warn(s);
    }

    @Override
    public void warn(String s, Object o) {
        log.warn(s, o);
    }

    @Override
    public void warn(String s, Object o, Object o1) {
        log.warn(s, o, o1);
    }

    @Override
    public void warn(String s, Object... objects) {
        log.warn(s, objects);
    }

    @Override
    public void error(String s) {
        log.error(s);
    }

    @Override
    public void error(String s, Object o) {
        log.error(s, o);
    }

    @Override
    public void error(String s, Object o, Object o1) {
        log.error(s, o, o1);
    }

    @Override
    public void error(String s, Object... objects) {
        log.error(s, objects);
    }
}
