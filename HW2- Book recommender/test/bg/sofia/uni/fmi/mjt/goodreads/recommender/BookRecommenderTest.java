package bg.sofia.uni.fmi.mjt.goodreads.recommender;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.SimilarityCalculator;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.genres.GenresOverlapSimilarityCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BookRecommenderTest {

    private static final String [] narniaTokens1 = {"1", "The Chronicles of Narnia",
            "C.S. Lewis", "\"adventure, magic, witch, wardrobe, lion, magic\"",
            "\"['Fantasy', 'Young Adult', 'Adventure', 'Childrens', 'Middle Grade']\"",
            "4.27", "617,006", "link1" };

    private static final String [] narniaTokens2 = {"2","The Lion, the Witch and the Wardrobe",
            "C.S. Lewis", "\"door, wardrobe, lion, winter, witch, wardrobe\"",
            "\"['Fantasy', 'Young Adult', 'Adventure', 'Fiction']\"", "4.23", "2,620,424", "link2"};

    private static final String [] pridePrejudiceTokens= {"3", "Pride and Prejudice", "Jane Austen",
            "\" Love, romantic clash, flirtation and intrigue.\"",
            "\"['Classics', 'Romance', 'Historical Fiction']\"", "4.28", "3,944,155", "link3"};

    private SimilarityCalculator calculator;
    private BookRecommender recommender;
    private Book narniaBook1, narniaBook2, prideBook;

    @BeforeEach
    void setUpBookRecommender() {
      narniaBook1 = Book.of(narniaTokens1);
      narniaBook2 = Book.of(narniaTokens2);
      prideBook = Book.of(pridePrejudiceTokens);

      Set<Book> books = Set.of(narniaBook1, narniaBook2, prideBook);
      calculator = new GenresOverlapSimilarityCalculator();
      recommender = new BookRecommender(books, calculator);
    }

    @Test
    void testThrowsWhenBookIsNull() {
        int sampleMaxN = 5;
        assertThrows(IllegalArgumentException.class,
                () -> recommender.recommendBooks(null, sampleMaxN),
                "IllegalArgumentException expected to be thrown when" +
                        "origin book is null!");
    }

    @Test
    void testThrowsWhenMaxIsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> recommender.recommendBooks(narniaBook1,-1),
                "IllegalArgumentException expected to be thrown " +
                        "when max is negative!");
    }

    @Test
    void testThrowsWhenMaxIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> recommender.recommendBooks(narniaBook1,0),
                "IllegalArgumentException expected to be thrown " +
                        "when max is null!");
    }

    @Test
    void testRecommendedBooksOrder() {
        int maxN = 2;
        SortedMap<Book, Double> resultBooks = recommender.recommendBooks(narniaBook1, maxN);

        assertEquals(resultBooks.size(), maxN);
        assertEquals(resultBooks.firstKey(), narniaBook1);
        assertEquals(resultBooks.lastKey(), narniaBook2);
    }

    @Test
    void testWhenInitializationDataIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
        { new BookRecommender(null, null); },
                "IllegalArgumentException expected " +
                        "when passing null pointer(s)" +
                        "to recommender's constructor!");
    }

    @Test
    void testWhenInitializationSetIsEmpty() {
        Set<Book> emptySet = new HashSet<>();
        assertThrows(IllegalArgumentException.class, () ->
                { new BookRecommender(emptySet, calculator); },
                "IllegalArgumentException expected " +
                        "when passing an empty book set" +
                        "to recommender's constructor!");
    }
}
