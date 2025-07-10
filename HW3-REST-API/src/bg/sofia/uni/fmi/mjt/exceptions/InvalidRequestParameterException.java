package bg.sofia.uni.fmi.mjt.exceptions;

public class InvalidRequestParameterException extends Exception {

    public InvalidRequestParameterException(String message) {
        super(message);
    }

    public InvalidRequestParameterException(String message, Throwable cause) {
        super(message, cause);
    }
}