package bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.descriptions;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.tokenizer.TextTokenizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import static java.lang.Math.log10;
import static org.junit.jupiter.api.Assertions.*;


public class TFIDFSimilarityCalculatorTest {

    private final double OCCURRING_ONCE = 1;
    private final double OCCURRING_TWICE = 2;

    private static final String [] tokens1 = {"1", "The Chronicles of Narnia",
            "C.S. Lewis", "\"adventure, magic, witch, wardrobe, lion, magic\"",
            "\"['Fantasy', 'Young Adult', 'Adventure']\"", "4.27", "617,006", "link1" };

    private static final String [] tokens2 = {"2","The Lion, the Witch and the Wardrobe",
            "C.S. Lewis", "\"door, wardrobe, lion, winter, witch, wardrobe\"",
            "\"['Fantasy', 'Young Adult', 'Adventure']\"", "4.23", "2,620,424", "link2"};

    private static final String [] tokens3 = {"3", "Pride and Prejudice", "Jane Austen",
            "\" Love, romantic clash, flirtation and intrigue.\"",
            "\"['Classics', 'Romance', 'Historical Fiction']\"", "4.28", "3,944,155", "link3"};

    private TFIDFSimilarityCalculator calculator;
    private TextTokenizer tokenizer;
    private Book narniaBook1, narniaBook2, pridePrejudice;

    @BeforeEach
    void setUpTFIDFCalculator() {
        narniaBook1 = Book.of(tokens1);
        narniaBook2 = Book.of(tokens2);
        pridePrejudice = Book.of(tokens3);
        Set<Book> books = Set.of(narniaBook1, narniaBook2, pridePrejudice);

        Set<String> stopWords = Set.of("a","one", "am", "are","and", "of",
                "the", "in","its", "has", "her",
                "as", "own", "most", "this");
        tokenizer = new TextTokenizer(stopWords);
        calculator = new TFIDFSimilarityCalculator(books, tokenizer);
    }

    @Test
    void testThrowsWhenIInitializationSetIsNull() {
        assertThrows(IllegalArgumentException.class, ()->
        { new TFIDFSimilarityCalculator(null, null);},
                "IllegalArgumentException expected when passing" +
                        " null pointer to TFIDF constructor.");
    }

    @Test
    void testThrowsWhenIInitializationSetIsEmpty() {
        Set<Book> fictionalSet = new HashSet<>();
        assertThrows(IllegalArgumentException.class, ()->
                { new TFIDFSimilarityCalculator(fictionalSet, tokenizer);},
                "IllegalArgumentException expected when passing" +
                        "an empty set to TFIDF constructor.");
    }

    @Test
    void testComputeBookTF() {
        int descriptionSize = 6;
        double adventureTF = OCCURRING_ONCE/ descriptionSize;
        double magicTF = OCCURRING_TWICE/ descriptionSize;
        double witchTF = OCCURRING_ONCE/ descriptionSize;
        double wardrobeTF = OCCURRING_ONCE/ descriptionSize;
        double lionTF = OCCURRING_ONCE/ descriptionSize;

        Map<String, Double> expectedResult = new HashMap<>();
        expectedResult.put("magic", magicTF);
        expectedResult.put("witch", witchTF);
        expectedResult.put("lion", lionTF);
        expectedResult.put("wardrobe", wardrobeTF);
        expectedResult.put("adventure", adventureTF);

        assertEquals(calculator.computeTF(narniaBook1), expectedResult);
    }

    @Test
    void testComputeIDF() {
        double totalBooks = 3;
        double adventureIDF = log10(totalBooks);
        double magicIDF = log10(totalBooks);
        double witchIDF = log10(totalBooks/OCCURRING_TWICE);
        double wardrobeIDF = log10(totalBooks/OCCURRING_TWICE);
        double lionIDF = log10(totalBooks/OCCURRING_TWICE);

        Map<String, Double> expectedResult = new HashMap<>();
        expectedResult.put("magic", magicIDF);
        expectedResult.put("witch", witchIDF);
        expectedResult.put("lion", lionIDF);
        expectedResult.put("wardrobe", wardrobeIDF);
        expectedResult.put("adventure", adventureIDF);
        Map<String, Double> res = calculator.computeIDF(narniaBook1);
        assertEquals(calculator.computeIDF(narniaBook1), expectedResult);
    }

    @Test
    void testComputeBookTFIDF() {
        Map<String, Double> computedTF = calculator.computeTF(narniaBook1);
        Map<String, Double> computedIDF = calculator.computeIDF(narniaBook1);
        Map<String, Double> expectedResult = new HashMap<>();

        for (Map.Entry<String, Double> entry: computedTF.entrySet()) {
            double valueTFIDF = entry.getValue() *
                    computedIDF.get(entry.getKey());
            expectedResult.put(entry.getKey(), valueTFIDF);
        }
        assertEquals(calculator.computeTFIDF(narniaBook1), expectedResult);

        System.out.println();
    }

    @Test
    void testComputeTFThrowsWhenNull() {
        assertThrows(IllegalArgumentException.class, () ->
                calculator.computeTF(null), "Exception to be " +
                "thrown by the TF method expected when book is null!");
    }

    @Test
    void testComputeIDFThrowsWhenNull() {
        assertThrows(IllegalArgumentException.class, () ->
                calculator.computeIDF(null), "Exception to be " +
                "thrown by the IDF method expected when book is null!");
    }

    @Test
    void testComputeTFIDFThrowsWhenNull() {
        assertThrows(IllegalArgumentException.class, () ->
                calculator.computeTFIDF(null), "Exception to be " +
                "thrown by the TF-IDF method expected when book is null!");
    }

    @Test
    void testCalculateSimilarityOfSameBooks() {
        assertEquals(calculator.calculateSimilarity(pridePrejudice, pridePrejudice), 1);
    }

    @Test
    void testCalculateSimilarityOfSimilarBooks() {
        assertTrue(Double.compare(calculator.calculateSimilarity
                        (narniaBook1, narniaBook2), 0.10) == 1,
                "High similarity of books of the same series expected.");
    }

    @Test
    void testCalculateSimilarityOfDifferentBooks() {
     assertTrue(Double.compare(calculator.calculateSimilarity
                     (narniaBook1, pridePrejudice), 0.0) == 0,
             "Low similarity of quite different books expected.");
    }
}
