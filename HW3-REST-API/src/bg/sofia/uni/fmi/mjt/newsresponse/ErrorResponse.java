package bg.sofia.uni.fmi.mjt.newsresponse;

import bg.sofia.uni.fmi.mjt.exceptions.BadRequestException;
import bg.sofia.uni.fmi.mjt.exceptions.ServerErrorException;
import bg.sofia.uni.fmi.mjt.exceptions.TooManyRequestsException;
import bg.sofia.uni.fmi.mjt.exceptions.UnauthorizedException;
import com.google.gson.Gson;
import java.net.http.HttpResponse;
import java.rmi.UnexpectedException;

public class ErrorResponse {
    private static final int BAD_REQUEST_CODE = 400;
    private static final int UNAUTHORIZED_CODE = 401;
    private static final int TOO_MANY_REQUESTS_CODE = 429;
    private static final int SERVER_ERROR_CODE = 500;
    private static final String ERROR_MESSAGE = "Unsuccessful request ends with code %s, message: %s";

    final transient private int httpStatus;
    private final String code;
    private final String message;


    public ErrorResponse (HttpResponse<String> response) {
        Gson gson = new Gson();
        ErrorResponse result = gson.fromJson(response.body(), ErrorResponse.class);
        this.httpStatus = response.statusCode();
        this.code = result.code;
        this.message = result.message;

    }

    public void dealWithError() throws Exception {
       switch (httpStatus) {
           case BAD_REQUEST_CODE -> throw new BadRequestException(String.
                   format(ERROR_MESSAGE, code, message));
           case UNAUTHORIZED_CODE -> throw new UnauthorizedException(String.
                   format(ERROR_MESSAGE, code, message));
           case TOO_MANY_REQUESTS_CODE -> throw new TooManyRequestsException(String.
                   format(ERROR_MESSAGE, code, message));
           case SERVER_ERROR_CODE -> throw new ServerErrorException(String.
                   format(ERROR_MESSAGE, code, message));
           default -> throw new UnexpectedException(String.format
                   ("An unthought-of error occurred with code %s, message: %s", code, message));
       }

    }
}
