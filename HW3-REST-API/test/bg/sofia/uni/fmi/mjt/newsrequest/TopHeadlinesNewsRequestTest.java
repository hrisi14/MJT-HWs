package bg.sofia.uni.fmi.mjt.newsrequest;

import bg.sofia.uni.fmi.mjt.exceptions.InvalidRequestParameterException;
import bg.sofia.uni.fmi.mjt.newrequest.TopHeadlinesNewsRequest;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class TopHeadlinesNewsRequestTest {
    private static final String API_KEY = "123456789abcdef";
    private static final List<String> KEYWORDS = List.of("breaking", "news");

    @Test
    void testWithKeywordsOnly() {
        try {
            TopHeadlinesNewsRequest request = TopHeadlinesNewsRequest.builder(API_KEY, KEYWORDS)
                    .build();

            assertEquals(KEYWORDS, request.getKeywords());

            String expectedQuery = "q=breaking+news&apiKey=%s".formatted(API_KEY);
            assertEquals(request.getUriQuery(), expectedQuery);
        } catch (Exception e) {
            fail("Exception thrown during test of query with keywords only.", e);
        }
    }

    @Test
    void testWithCountryAndCategory() {
        try {
            TopHeadlinesNewsRequest request = TopHeadlinesNewsRequest.builder(API_KEY, KEYWORDS)
                    .setNewsCountry("us")
                    .setNewsCategory("business")
                    .build();

            assertEquals("us", request.getCountry().country());
            assertEquals("business", request.getCategory().category());

            String expectedQuery = "q=breaking+news&country=us&category=business&apiKey=%s".formatted(API_KEY);
            assertEquals(request.getUriQuery(), expectedQuery);
        } catch (Exception e) {
            fail("Exception thrown during test of query with news and category.", e);
        }
    }

    @Test
    void testWithPagination() {
        try {
            TopHeadlinesNewsRequest request = TopHeadlinesNewsRequest.builder(API_KEY, KEYWORDS)
                    .setPage(3)
                    .setPageSize(15)
                    .build();

            assertEquals(3, request.getPage());
            assertEquals(15, request.getPageSize());

            String expectedQuery = "q=breaking+news&pageSize=15&page=3&apiKey=%s".formatted(API_KEY);
            assertEquals(request.getUriQuery(), expectedQuery);
        } catch (Exception e) {
            fail("Exception thrown during test of set page and pageSize.", e);
        }
    }

    @Test
    void testWithSources() {
        try {
            TopHeadlinesNewsRequest request = TopHeadlinesNewsRequest.builder(API_KEY, KEYWORDS)
                    .setSources("bbc-news")
                    .build();

            assertEquals("bbc-news", request.getSources());

            String expectedQuery = "q=breaking+news&sources=bbc-news&apiKey=%s".formatted(API_KEY);
            assertEquals(request.getUriQuery(), expectedQuery);
        } catch (Exception e) {
            fail("Exception thrown during test of query with sources only.", e);
        }
    }

    @Test
    void testInvalidParameters() {
        assertThrows(InvalidRequestParameterException.class,
                () -> TopHeadlinesNewsRequest.builder(null, KEYWORDS));
        assertThrows(InvalidRequestParameterException.class,
                () -> TopHeadlinesNewsRequest.builder(API_KEY, null));
        assertThrows(InvalidRequestParameterException.class,
                () -> TopHeadlinesNewsRequest.builder(API_KEY, List.of()).build());
        assertThrows(InvalidRequestParameterException.class,
                () -> TopHeadlinesNewsRequest.builder(API_KEY, KEYWORDS).setPage(-1));
        assertThrows(InvalidRequestParameterException.class,
                () -> TopHeadlinesNewsRequest.builder(API_KEY, KEYWORDS).setPageSize(0));
        assertThrows(InvalidRequestParameterException.class,
                () -> TopHeadlinesNewsRequest.builder(API_KEY, KEYWORDS).setNewsCategory("nothing"));
        assertThrows(InvalidRequestParameterException.class,
                () -> TopHeadlinesNewsRequest.builder(API_KEY, KEYWORDS).setNewsCountry("no such country"));
    }

    @Test
    void testURIConstruction() {
        try {
            TopHeadlinesNewsRequest request = TopHeadlinesNewsRequest.builder(API_KEY, KEYWORDS)
                    .setNewsCountry("gb")
                    .setPage(2)
                    .setPageSize(20)
                    .build();

            URI uri = request.getUri();
            assertNotNull(uri);

            String expectedScheme = "https";
            String expectedHost = "newsapi.org";
            String expectedPath = "/v2/top-headlines";
            assertEquals(expectedScheme, uri.getScheme());
            assertEquals(expectedHost, uri.getHost());
            assertEquals(expectedPath, uri.getPath());
        } catch (Exception e) {
            fail("Error occurred while testing URI construction.", e);
        }
    }
}
