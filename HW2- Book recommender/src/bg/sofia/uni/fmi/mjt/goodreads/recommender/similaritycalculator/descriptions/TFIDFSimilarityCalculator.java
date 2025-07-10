package bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.descriptions;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.SimilarityCalculator;
import bg.sofia.uni.fmi.mjt.goodreads.tokenizer.TextTokenizer;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TFIDFSimilarityCalculator implements SimilarityCalculator {

    private final Set<Book> books;
    private final TextTokenizer tokenizer;

    public TFIDFSimilarityCalculator(Set<Book> books, TextTokenizer tokenizer) {
        if (books == null || books.isEmpty() || tokenizer == null) {
            throw new IllegalArgumentException("Invalid data " +
                    "passed to TFIDF calculator's constructor!");
        }
        this.books = new HashSet<>(books);
        this.tokenizer = tokenizer;
    }

    /*
     * Do not modify!
     */
    @Override
    public double calculateSimilarity(Book first, Book second) {
        Map<String, Double> tfIdfScoresFirst = computeTFIDF(first);
        Map<String, Double> tfIdfScoresSecond = computeTFIDF(second);

        return cosineSimilarity(tfIdfScoresFirst, tfIdfScoresSecond);
    }

    public Map<String, Double> computeTFIDF(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book for " +
                    "TF computing must not be null!");
        }

        Map<String, Double> computedTF = computeTF(book);
        Map<String, Double> computedIDF = computeIDF(book);
        return computedTF.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                       entry -> entry.getValue() * computedIDF.get(entry.getKey())));
    }

    public Map<String, Double> computeTF(Book book) {  //does this book have to be from the Set of Books
        if (book == null) {
            throw new IllegalArgumentException("Book for " +
                    "TF computing must not be null!");
        }
        List<String> bookDescription = tokenizer.tokenize(book.description());

        return bookDescription.stream().distinct().collect(Collectors.toMap(word ->
                        word, word -> computeWordTF(word, bookDescription)));
    }

    public Map<String, Double> computeIDF(Book book) { //TO DO: try to simplify it with flatMap!!!
        if (book == null) {
            throw new IllegalArgumentException("Book for " +
                    "TF computing must not be null!");
        }

        List<String> bookDescription = tokenizer.tokenize(book.description());
        return bookDescription.stream().distinct().collect(Collectors.toMap(word -> word,
                this::computeWordIDF));
    }

    private double computeWordIDF(String word) {
        double wordOccurrences = books.stream().filter(book ->
                tokenizer.tokenize(book.description()).contains(word)).count();
        double booksCount = books.size();
        return Math.log10(booksCount / wordOccurrences);
    }

    private double computeWordTF(String word,  List<String> bookDescription) { //private or public???
        long wordOccurrences = bookDescription.stream().filter(current ->
                current.equals(word)).count();
        return (double) wordOccurrences / bookDescription.size();
    }

    private double cosineSimilarity(Map<String, Double> first, Map<String, Double> second) {
        double magnitudeFirst = magnitude(first.values());
        double magnitudeSecond = magnitude(second.values());

        return dotProduct(first, second) / (magnitudeFirst * magnitudeSecond);
    }

    private double dotProduct(Map<String, Double> first, Map<String, Double> second) {
        Set<String> commonKeys = new HashSet<>(first.keySet());
        commonKeys.retainAll(second.keySet());

        return commonKeys.stream()
                .mapToDouble(word -> first.get(word) * second.get(word))
                .sum();
    }

    private double magnitude(Collection<Double> input) {
        double squaredMagnitude = input.stream()
                .map(v -> v * v)
                .reduce(0.0, Double::sum);

        return Math.sqrt(squaredMagnitude);
    }
}