package bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.composite;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.SimilarityCalculator;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.descriptions.TFIDFSimilarityCalculator;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.genres.GenresOverlapSimilarityCalculator;
import bg.sofia.uni.fmi.mjt.goodreads.tokenizer.TextTokenizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CompositeSimilarityCalculatorTest {

    private static final String [] tokens1 = {"1", "The Chronicles of Narnia",
            "C.S. Lewis", "\"adventure, magic, witch, wardrobe, lion, magic\"",
            "\"['Fantasy', 'Young Adult', 'Adventure', 'Childrens', 'Middle Grade']\"",
            "4.27", "617,006", "link1" };

    private static final String [] tokens2 = {"2","The Lion, the Witch and the Wardrobe",
            "C.S. Lewis", "\"door, wardrobe, lion, winter, witch, wardrobe\"",
            "\"['Fantasy', 'Young Adult', 'Adventure', 'Fiction']\"", "4.23", "2,620,424", "link2"};

    private static final String [] tokens3 = {"3", "Pride and Prejudice", "Jane Austen",
            "\" Love, romantic clash, flirtation and intrigue.\"",
            "\"['Classics', 'Romance', 'Historical Fiction']\"", "4.28", "3,944,155", "link3"};

    private static final double CALCULATOR_WEIGHT = 0.5;
    private static final double PRIDE_PREJUDICE_SAME = 1;
    private static final double NARNIA_PREJUDICE = 0.0;
    private static final double TFIDF_NARNIA = 0.1395787486934462;
    private static final double OVERLAP_NARNIA = (double) 3 /4;

    private Book narniaBook1, narniaBook2, prideBook;
    private Map<SimilarityCalculator, Double> similarityCalculatorMap;
    private CompositeSimilarityCalculator calculator;

    @BeforeEach
    void setUpCompositeCalculator() {
        narniaBook1 = Book.of(tokens1);
        narniaBook2 = Book.of(tokens2);
        prideBook = Book.of(tokens3);
        Set<Book> books = Set.of(narniaBook1, narniaBook2, prideBook);

        TFIDFSimilarityCalculator tfidfCalculator = new TFIDFSimilarityCalculator
                (books, new TextTokenizer(Set.of("a")));
        GenresOverlapSimilarityCalculator overlapCalculator = new GenresOverlapSimilarityCalculator();
        similarityCalculatorMap = new HashMap<>();
        similarityCalculatorMap.put(tfidfCalculator, 0.5);
        similarityCalculatorMap.put(overlapCalculator, 0.5);

        calculator = new CompositeSimilarityCalculator(similarityCalculatorMap);
    }

    @Test
    void testCompositeSimilarityOfSameBook() {
        assertEquals(calculator.calculateSimilarity(prideBook, prideBook), PRIDE_PREJUDICE_SAME);
    }

    @Test
    void testCompositeSimilarityOfSimilarBooks() {
        double expectedValue = TFIDF_NARNIA * CALCULATOR_WEIGHT +
                OVERLAP_NARNIA * CALCULATOR_WEIGHT;
       assertEquals(Double.compare(calculator.calculateSimilarity
               (narniaBook1, narniaBook2), expectedValue),0);
    }

    @Test
    void testCompositeSimilarityOfDifferentBooks() {
       assertEquals(Double.compare(calculator.calculateSimilarity
                (narniaBook1, prideBook), NARNIA_PREJUDICE),0);
    }

    @Test
    void testThrowsWhenCalculatorsMapIsNull() {
        assertThrows(NullPointerException.class, () ->
        {new CompositeSimilarityCalculator(null);},
                "NullPointerException expected to be thrown " +
                        "when passing null pointer to composite calculator's " +
                        "constructor!");
    }

    @Test
    void testThrowsWhenBookNull() {
        assertThrows(IllegalArgumentException.class, () ->
                calculator.calculateSimilarity(null, narniaBook2),
                "IllegalArgumentException expected to be " +
                        "thrown when comparing null-poiter books!");
    }
}
