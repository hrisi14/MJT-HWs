package bg.sofia.uni.fmi.mjt.newsresponse;

import com.google.gson.Gson;
import java.net.http.HttpResponse;

public class ResponseManager {

    private static final String ERROR_STATUS = "error";

    public static NewsResponse manageResponse(HttpResponse<String> response) throws Exception {
        Gson gson = new Gson();
        var result = gson.fromJson(response.body(), TopHeadlinesNewsResponse.class);
        if (result.getStatus().equals(ERROR_STATUS)) {
            ErrorResponse errorResponse = new ErrorResponse (response);
            errorResponse.dealWithError();
        }
        return TopHeadlinesNewsResponse.of(response);
    }
}
