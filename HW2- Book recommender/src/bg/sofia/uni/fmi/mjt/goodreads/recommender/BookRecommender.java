package bg.sofia.uni.fmi.mjt.goodreads.recommender;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.SimilarityCalculator;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.SortedMap;
import java.util.stream.Collectors;

public class BookRecommender implements BookRecommenderAPI {

    private final Set<Book> initialBooks;
    private final SimilarityCalculator calculator;

    public BookRecommender(Set<Book> initialBooks, SimilarityCalculator calculator) {
        if (initialBooks == null || initialBooks.isEmpty()
            || calculator == null) {
            throw new IllegalArgumentException("Invalid parameters" +
                   "passed for BookRecommender's construction!");
        }

        this.initialBooks = initialBooks;
        this.calculator = calculator;
    }

    @Override
    public SortedMap<Book, Double> recommendBooks(Book origin, int maxN) {
        if (origin == null) {
            throw new IllegalArgumentException("Origin book must no be null!");
        }

        if (maxN <= 0) {
            throw new IllegalArgumentException("Max number of entries " +
                    "must not be null of negative number!");
        }
        return initialBooks.stream()
                .map(book -> Map.entry(book,
                        calculator.calculateSimilarity(origin, book)))
                .sorted((entry1, entry2) ->
                        Double.compare(entry2.getValue(), entry1.getValue())).limit(maxN)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, () -> new TreeMap<>(
                                Comparator.<Book>comparingDouble(book ->
                                        calculator.calculateSimilarity(origin, book)).reversed())));
    }
}
