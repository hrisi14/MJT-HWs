package bg.sofia.uni.fmi.mjt.newsresponse;

import bg.sofia.uni.fmi.mjt.article.Article;
import com.google.gson.Gson;

import java.net.http.HttpResponse;
import java.util.List;

public class TopHeadlinesNewsResponse implements NewsResponse {

    private String status;
    private int totalResults;
    private List<Article> articles;

    public static TopHeadlinesNewsResponse of(HttpResponse<String> response) {
        Gson gson = new Gson();
        return gson.fromJson(response.body(), TopHeadlinesNewsResponse.class);
    }

    @Override
    public int getTotalResults() {
        return totalResults;
    }

    @Override
    public List<Article> getArticles() {
        return articles;
    }

    @Override
    public String getStatus() {
        return status;
    }
}
