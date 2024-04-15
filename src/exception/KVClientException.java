package exception;

public class KVClientException extends Exception {

    public KVClientException(String message) {
        super(message);
    }

    public KVClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
