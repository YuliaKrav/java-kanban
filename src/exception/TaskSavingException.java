package exception;

public class TaskSavingException extends RuntimeException {

    public TaskSavingException(String message, Throwable cause) {
        super(message, cause);
    }
}
