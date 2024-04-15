package exception;

public class TaskLoadingException extends RuntimeException {

    public TaskLoadingException(String message, Throwable cause) {
        super(message, cause);
    }
}
