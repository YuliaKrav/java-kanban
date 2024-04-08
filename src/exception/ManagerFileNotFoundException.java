package exception;

public class ManagerFileNotFoundException extends RuntimeException {

    public ManagerFileNotFoundException(String message) {
        super(message);
    }
}
