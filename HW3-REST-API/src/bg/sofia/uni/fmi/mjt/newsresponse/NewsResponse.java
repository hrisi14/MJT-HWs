package bg.sofia.uni.fmi.mjt.newsresponse;

import bg.sofia.uni.fmi.mjt.article.Article;
import java.util.List;

public interface NewsResponse {

    int getTotalResults();
    List<Article> getArticles();
    String getStatus();
}
