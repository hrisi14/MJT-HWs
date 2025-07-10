package bg.sofia.uni.fmi.mjt.newrequest;

import bg.sofia.uni.fmi.mjt.exceptions.InvalidRequestParameterException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;


public class TopHeadlinesNewsRequest implements NewsRequest {

    private final String apiKey;
    private final List<String> keywords;
    private final NewsCountry country;
    private final NewsCategory category;
    private final String sources;

    private final int pageSize;
    private final int page;

    //да добавя изискването keywords да е задължителен параметър на request въпреки примера в API-то!!!

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (!(otherObject instanceof TopHeadlinesNewsRequest that)) {
            return false;
        }
        return  Objects.equals(this.keywords, that.keywords) &&
                Objects.equals(this.country, that.country) &&
                Objects.equals(this.category, that.category) &&
                Objects.equals(this.sources, that.sources);
    }

    @Override
    public int hashCode() {
        return Objects.hash(apiKey, keywords, country, category, sources);
    }

    //I won't put a getter for the apiKey since it is confidential information
    @Override
    public List<String> getKeywords() {
        return keywords;
    }

    @Override
    public NewsCountry getCountry() {
        return country;
    }

    @Override
    public NewsCategory getCategory() {
        return category;
    }

    @Override
    public String getSources() {
        return sources;
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public URI getUri() {
        try {
            return new URI(API_ENDPOINT_SCHEME, API_ENDPOINT_HOST, API_ENDPOINT_PATH,
                    getUriQuery(), null);
        } catch (URISyntaxException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public String getUriQuery() {
        StringBuilder strBuilder = new StringBuilder();

        strBuilder.append(String.format("q=%s&", String.join("+", keywords)));

        if(country != NewsCountry.DEFAULT) {
            strBuilder.append(String.format("country=%s&", country.country()));
        }
        if(category != NewsCategory.DEFAULT) {
            strBuilder.append(String.format("category=%s&", category.category()));
        }

        if (!sources.isBlank()) {
            strBuilder.append(String.format("sources=%s&", sources));
        }

        if (pageSize > 0 && pageSize != DEFAULT_RESULTS_PER_PAGE) {
            strBuilder.append("pageSize=%d&".formatted(this.pageSize));
        }

        if (page > 0 && page != DEFAULT_PAGE_NUMBER) {
            strBuilder.append("page=%d&".formatted(this.page));
        }

        strBuilder.append(String.format(API_ENDPOINT_API_KEY, apiKey));
        return strBuilder.toString();
    }

    public static NewsRequestBuilder  builder(String apiKey, List<String> keywords)
            throws InvalidRequestParameterException {
        if (apiKey == null || apiKey.isBlank() || apiKey.isEmpty()) {
            throw new InvalidRequestParameterException("Invalid API key passed to a request");
        }

        if (keywords == null || keywords.isEmpty()) {
            throw new InvalidRequestParameterException("Keywords are a compulsory " +
                    "parameter for a request!");
        }
        return new NewsRequestBuilder(apiKey, keywords);
    }



    private TopHeadlinesNewsRequest(NewsRequestBuilder builder) {
       this.apiKey = builder.apiKey;
       this.keywords = builder.keywords;
       this.country = builder.country;
       this.category = builder.category;
       this.sources = builder.sources;
        this.pageSize = builder.pageSize;
        this.page = builder.page;
    }

       public static class NewsRequestBuilder {

        private final String apiKey;
        private final List<String> keywords;

        private NewsCountry country;
        private NewsCategory category;
        private String sources;
        private int pageSize;
        private int page;



        private NewsRequestBuilder(String apiKey, List<String> keywords) {
            this.apiKey = apiKey;
            this.keywords = keywords;

            this.category = NewsCategory.DEFAULT;
            this.country = NewsCountry.DEFAULT;
            this.sources = DEFAULT_SOURCES;
            this.pageSize = DEFAULT_RESULTS_PER_PAGE;
            this.page = DEFAULT_PAGE_NUMBER;
        }

       public NewsRequestBuilder setNewsCategory(String category) throws InvalidRequestParameterException {

            if (category == null || category.isEmpty() || category.isBlank()) {
                this.category = NewsCategory.DEFAULT;
                return this;
            }

            try {
                this.category = NewsCategory.valueOf(category.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvalidRequestParameterException("Non-existent" +
                        "news category passed to request", e);
            }
            return this;
        }


        public NewsRequestBuilder setNewsCountry(String country) throws InvalidRequestParameterException {

            if (country == null || country.isEmpty() || country.isBlank()) {
                this.country = NewsCountry.DEFAULT;
                return this;
            }
            try {
                this.country = NewsCountry.valueOf(country);
            } catch (IllegalArgumentException e) {
                throw new InvalidRequestParameterException("Non-existent" +
                        "news country passed to request", e);
            }
            return this;
        }

        public NewsRequestBuilder setSources(String sources) throws InvalidRequestParameterException {

            if (sources != null && !sources.isEmpty() && !sources.isBlank()) {
                this.sources = sources;
                return this;
            }
            throw new InvalidRequestParameterException("Entered sources can not be null or empty!");
        }

        public NewsRequestBuilder setPageSize(int pageSize) throws InvalidRequestParameterException {
            if (pageSize > 0 && pageSize <= MAX_RESULTS_PAGE) {
                this.pageSize = pageSize;
                return this;
            }
          throw new InvalidRequestParameterException(String.format("Page's size " +
                  "parameter must have a value between 1 and 100. " +
                  "You have requested %d.", pageSize));
        }

        public NewsRequestBuilder setPage(int page) throws InvalidRequestParameterException {
            if (page > 0 && page < MAX_RESULTS_PAGE) {
                this.page = page;
                return this;
            }
           throw new InvalidRequestParameterException(String.format("Page " +
                   "parameter must have a value between 1 and 100. You have requested %d.", page));
        }

        public TopHeadlinesNewsRequest build() {
            return new TopHeadlinesNewsRequest(this);
        }
    }
}