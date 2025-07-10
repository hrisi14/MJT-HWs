package bg.sofia.uni.fmi.mjt.goodreads.tokenizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TextTokenizer {

    private final Set<String> stopwords;

    public TextTokenizer(Reader stopwordsReader) {
        try (var br = new BufferedReader(stopwordsReader)) {
            stopwords = br.lines().collect(Collectors.toSet());
        } catch (IOException ex) {
            throw new IllegalArgumentException("Could not load dataset", ex);
        }
    }

    public TextTokenizer(Set<String> stopwords) {  //I used this constructor mainly for testing purposes
        if (stopwords == null || stopwords.isEmpty()) {
            throw new IllegalArgumentException("Initialization set " +
                     "of stop words must not be null or empty!");
        }
        this.stopwords = stopwords;
    }

    public List<String> tokenize(String input) {
        if (input == null || input.isEmpty() || input.isBlank()) {
            throw new IllegalArgumentException("String passed for" +
                   " tokenizing must not be null, blank or empty!");
        }
        String refactoredInput = input.trim().replaceAll("\\p{Punct}",
               "").replaceAll("\\s+", " ").toLowerCase();

        String [] derivedWords = refactoredInput.split(" ");
        return Arrays.stream(derivedWords).filter(word ->
              !stopwords.contains(word)).toList();
    }

    public Set<String> stopwords() {
        return stopwords;
    }

}