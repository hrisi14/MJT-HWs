package bg.sofia.uni.fmi.mjt.newsresponse;

import org.mockito.Mockito;

import java.net.http.HttpResponse;

public class ResponseManagerTest {
    private static final HttpResponse<String> mockRequest =
            (HttpResponse<String>) Mockito.mock(HttpResponse.class);
}
