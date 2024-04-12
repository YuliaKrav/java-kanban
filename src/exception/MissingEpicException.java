package exception;

public class MissingEpicException extends RuntimeException {

    public MissingEpicException(String message) {
        super(message);
    }
}
