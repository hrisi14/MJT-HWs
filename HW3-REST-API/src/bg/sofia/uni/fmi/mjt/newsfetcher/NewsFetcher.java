package bg.sofia.uni.fmi.mjt.newsfetcher;

import bg.sofia.uni.fmi.mjt.article.Article;
import bg.sofia.uni.fmi.mjt.client.NewsHTTPClient;
import bg.sofia.uni.fmi.mjt.exceptions.InvalidRequestParameterException;
import bg.sofia.uni.fmi.mjt.newrequest.NewsRequest;
import bg.sofia.uni.fmi.mjt.newsresponse.NewsResponse;
import bg.sofia.uni.fmi.mjt.newsresponse.ResponseManager;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static bg.sofia.uni.fmi.mjt.newrequest.NewsRequest.*;

public class NewsFetcher {

    private static final ExecutorService executor;
    private static NewsHTTPClient client;
    private static final Map<NewsRequest, NewsResponse> cachedResults;

   static  {
       executor = Executors.newCachedThreadPool();
       cachedResults = new HashMap<>();
       client = new NewsHTTPClient(executor);
   }

   public void setClient(NewsHTTPClient newClient) {
       client = newClient;
   }

   public List<Article> fetchNewsFeed(NewsRequest request) throws InvalidRequestParameterException {
       if (request == null) {
           throw new InvalidRequestParameterException("Request can not be null!");
       }
       if (cachedResults.containsKey(request)) {
           return cachedResults.get(request).getArticles();
       }

       List<Article> allArticles = new ArrayList<>();
       int currentPage = 1;
       int pageSize = request.getPageSize() > 0 ? request.getPageSize() : DEFAULT_RESULTS_PER_PAGE;
       int maxResults = Math.min(MAX_TOTAL_RESULTS, pageSize * MAX_TOTAL_RESULTS);

       URI currentUri = request.getUri();
      try {
          while (allArticles.size() < maxResults) {
              HttpRequest httpRequest = HttpRequest.newBuilder().uri(currentUri).build();
              CompletableFuture<HttpResponse<String>> future = client.getClient().
                      sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());

              future.join();
              NewsResponse newsResponse = ResponseManager.manageResponse(future.get());

              List<Article> currentPageArticles = newsResponse.getArticles();
              if (currentPageArticles.isEmpty()) {
                  break;
              }
              allArticles.addAll(currentPageArticles);

              cachedResults.put(request, newsResponse);
              currentPage++;

              currentUri = updateURI(request, currentPage, pageSize);
          }
       } catch (Exception e) {
           throw new RuntimeException("Error while sending requests.", e);
       }
      return allArticles;
   }

    private URI updateURI(NewsRequest request, int page, int pageSize) {
        try {
            String queryWithPaging = request.getUriQuery().replaceAll("page=*",
                    "page=%s".formatted(page)).replaceAll("pageSize=*",
                    "pageSize=%s".formatted(pageSize));
            return new URI(API_ENDPOINT_SCHEME, API_ENDPOINT_HOST, API_ENDPOINT_PATH, queryWithPaging, null);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error constructing URI for paging.", e);
        }
    }
}
