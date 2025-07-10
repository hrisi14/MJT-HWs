package bg.sofia.uni.fmi.mjt.article;

import com.google.gson.annotations.SerializedName;

public record Article(Source source, String author, String title,
                      String description, String url, String urlToImage,
                      @SerializedName("publishedAt") String publishDate, String content) {

     public String toString() {
      return String.format("""
              Source:%s
               \
              Author:%s
              \
              Title:%s
              \
              Description:%s
              \
              URL:%s
              \
              UrlToImage:%s
              \
              Publication date:%s
              \
              Content:%s
              """,source.toString(), author, title, description, url,
              urlToImage, publishDate, content);
     }
}
