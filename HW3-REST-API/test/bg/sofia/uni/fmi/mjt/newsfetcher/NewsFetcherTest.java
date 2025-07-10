package bg.sofia.uni.fmi.mjt.newsfetcher;

import bg.sofia.uni.fmi.mjt.article.Article;
import bg.sofia.uni.fmi.mjt.client.NewsHTTPClient;
import bg.sofia.uni.fmi.mjt.exceptions.InvalidRequestParameterException;
import bg.sofia.uni.fmi.mjt.newrequest.NewsRequest;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//Credits to: https://github.com/w1nston19/Java/tree/main/bg/uni/sofia/fmi/mjt
// I have used these tests as an example

class NewsFetcherTest {
    private static final String myResponse = "{"
            + "\"status\": \"ok\","
            + "\"totalResults\": 3,"
            + "\"articles\": ["
            + "  { \"source\": {\"id\": \"bbc-news\", \"name\": \"BBC News\"},"
            + "    \"author\": \"BBC News\","
            + "    \"title\": \"Breaking news 1\","
            + "    \"description\": \"Sample description 1.\","
            + "    \"url\": \"https://example.com1\","
            + "    \"publishedAt\": \"2025-01-22T06:52:28Z\","
            + "    \"content\": \"Sample content1.\""
            + "  }," +
            " {"
            + "    \"source\": {\"id\": \"bbc-news\", \"name\": \"BBC News\"},"
            + "    \"author\": \"BBC News\","
            + "    \"title\": \"Breaking news 2\","
            + "    \"description\": \"Sample description 2.\","
            + "    \"url\": \"https://example.com2\","
            + "    \"publishedAt\": \"2025-01-22T06:52:25Z\","
            + "    \"content\": \"Sample content1.\""
            + "  },"
            + "  {"
            + "    \"source\": {\"id\": \"bbc-news\", \"name\": \"BBC News\"},"
            + "    \"author\": \"BBC News\","
            + "    \"title\": \"Breaking news 3\","
            + "    \"description\": \"Sample description 3.\","
            + "    \"url\": \"https://example.com3\","
            + "    \"publishedAt\": \"2025-01-22T06:37:18Z\","
            + "    \"content\": \"Sample content1.\""
            + "  }"
            + "]"
            + "}";

    private static final HttpClient mockHttpClient = mock(HttpClient.class);
    private static final NewsHTTPClient myClient = new NewsHTTPClient(mockHttpClient);
    private static final CompletableFuture<HttpResponse<String>> mockResponse =
            mock(CompletableFuture.class);

    private static final NewsFetcher newsFetcherTested = new NewsFetcher();

    static
     {
        newsFetcherTested.setClient(myClient);

        try {
            HttpResponse<String> httpResponseMock = mock(HttpResponse.class);
            when(httpResponseMock.statusCode()).thenReturn(200);
            when(httpResponseMock.body()).thenReturn(myResponse);
            when(mockResponse.get()).thenReturn(httpResponseMock);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        when(myClient.getClient().sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);
    }


    @Test
    void testFetchNewsFeed() {
        NewsRequest mockRequest = mock(NewsRequest.class);

        when(mockRequest.getUri()).thenReturn(URI.create("https://example.com/api/news"));
        when(mockRequest.getUriQuery()).thenReturn("query=world&pageSize=10&page=1");

        List<String> expectedHeadlines = List.of("Breaking News 1", "Breaking News 2", "Breaking News 3");
        List<String> actualHeadlines = new ArrayList<>();

        try {
            actualHeadlines = newsFetcherTested.fetchNewsFeed(mockRequest).stream()
                    .map(Article::title).limit(3)
                    .toList();
        } catch (Exception e) {
            fail("Exception thrown during fetchNewsFeed test: " + e.getMessage());
        }
        System.out.println(actualHeadlines);

        assertTrue(expectedHeadlines.containsAll(actualHeadlines) &&
                        actualHeadlines.containsAll(expectedHeadlines),
                "The result article headlines do not match the expected headlines.");
    }


    @Test
    void testFetchWithCachedRequest() {
        NewsRequest mockRequest = mock(NewsRequest.class);
        when(mockRequest.getUri()).thenReturn(URI.create("https://example.com/api/news"));
        when(mockRequest.getUriQuery()).thenReturn("query=world&pageSize=10&page=1");

        try {
            List<Article> firstFetch = newsFetcherTested.fetchNewsFeed(mockRequest);
            List<Article> secondFetch = newsFetcherTested.fetchNewsFeed(mockRequest);

            assertEquals(firstFetch, secondFetch, "The second fetch did not return the cached response.");
            verify(myClient.getClient(), times(1))
                    .sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
        } catch (Exception e) {
            fail("Exception has been thrown during test of cached results: " + e.getMessage());
        }
    }

    @Test
    void testFetchWithInvalidRequest() {
        assertThrows(InvalidRequestParameterException.class, () -> {
            newsFetcherTested.fetchNewsFeed(null);
        }, "Expected InvalidRequestParameterException to be " +
                "thrown when fetching with a null-request but nothing happened..");
    }
}
