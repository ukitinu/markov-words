package ukitinu.markovwords.repo;

import java.io.Serial;

public class DataException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 2426509960464436022L;

    public DataException(String message) {
        super(message);
    }

    public DataException(Throwable cause) {
        super(cause);
    }

    public DataException(String message, Throwable cause) {
        super(message, cause);
    }
}
