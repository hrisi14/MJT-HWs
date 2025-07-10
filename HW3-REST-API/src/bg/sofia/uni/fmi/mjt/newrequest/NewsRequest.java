package bg.sofia.uni.fmi.mjt.newrequest;

import java.net.URI;
import java.util.List;


public interface NewsRequest {
     String API_ENDPOINT_SCHEME = "https";
     String API_ENDPOINT_HOST = "newsapi.org";
     String API_ENDPOINT_PATH = "/v2/top-headlines";
     String API_ENDPOINT_API_KEY = "apiKey=%s";
     String DEFAULT_SOURCES = "";

     int DEFAULT_RESULTS_PER_PAGE = 20;
     int MAX_RESULTS_PAGE = 100;
     int DEFAULT_PAGE_NUMBER = 1;
     int MAX_PAGES = 100;
     int MAX_TOTAL_RESULTS = 100;

    List<String> getKeywords();

    NewsCountry getCountry();

    NewsCategory getCategory();

    String getSources();

    String getUriQuery();

    URI getUri();

    int getPage();

    int getPageSize();
}
