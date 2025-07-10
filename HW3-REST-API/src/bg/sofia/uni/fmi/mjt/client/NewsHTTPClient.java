package bg.sofia.uni.fmi.mjt.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.http.HttpClient;
import java.util.concurrent.Executor;

public class NewsHTTPClient {
    //да изнеса повечето логика в NewsFetcher- класа!!!
    //това трябва да е просто wrapper клас на HTTPClient с
    //член-данна API_KEY и методи за четенето и съхраняването й


    private static final String DEFAULT_API_KEY_FILE = "src" +  File.separator + "myFile";
    private final HttpClient httpClient;
    private final String apiKey;


    public NewsHTTPClient(HttpClient httpClient, String fileName) {
        if (httpClient != null && fileName != null
                && !fileName.isEmpty() && !fileName.isBlank()) {
            this.httpClient = httpClient;
            this.apiKey = loadKey(fileName);
        }
        else {
            throw new IllegalArgumentException("Invalid client's parameters!");
        }
    }

    public NewsHTTPClient(HttpClient httpClient) {
        if (httpClient != null) {
            this.httpClient = httpClient;
            this.apiKey = loadKey(DEFAULT_API_KEY_FILE);
        }
        else {
            throw new IllegalArgumentException("Invalid client's parameters!");
        }
    }

    public NewsHTTPClient(Executor executor) {
        this.httpClient = HttpClient.newBuilder().executor(executor).build();
        this.apiKey = loadKey(DEFAULT_API_KEY_FILE);
    }

    public HttpClient getClient() {
        return httpClient;
    }

    private String loadKey(String filePath) {
        String password = null;
           try (var reader = new BufferedReader(new FileReader(filePath))) {
            password = reader.readLine();
        } catch (IOException e) {
               throw new UncheckedIOException(String.format("A problem occurred " +
                       "while reading from file %s", filePath), e);
        }
           return password;
    }

}
