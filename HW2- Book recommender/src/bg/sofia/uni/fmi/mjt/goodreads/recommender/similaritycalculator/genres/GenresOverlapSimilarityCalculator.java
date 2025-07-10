package bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.genres;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.SimilarityCalculator;
import java.util.HashSet;
import java.util.Set;

public class GenresOverlapSimilarityCalculator implements SimilarityCalculator {

    @Override
    public double calculateSimilarity(Book first, Book second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Books passed for " +
                    "comparison must not be null!");
        }

        Set<String> firstGenres = new HashSet<>(first.genres());
        Set<String> secondGenres = new HashSet<>(second.genres());

        if (firstGenres.isEmpty() || secondGenres.isEmpty()) {
            return 1;
        }

        double minSize = Math.min(firstGenres.size(), secondGenres.size());
        firstGenres.retainAll(secondGenres);
        double commonElementsCount = firstGenres.size();

        return commonElementsCount / minSize;
    }

}