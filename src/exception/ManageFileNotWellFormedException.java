package exception;

public class ManageFileNotWellFormedException extends RuntimeException {

    public ManageFileNotWellFormedException(String message) {
        super(message);
    }
}