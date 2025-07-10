package bg.sofia.uni.fmi.mjt.exceptions;

public class TooManyRequestsException extends Exception {

    public TooManyRequestsException(String message) {
        super(message);
    }

    public TooManyRequestsException(String message, Throwable cause) {
        super(message, cause);
    }
}
