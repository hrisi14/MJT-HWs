package bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.genres;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GenresOverlapSimilarityCalculatorTest {

    private GenresOverlapSimilarityCalculator calculator;
    private Book narniaBook1, narniaBook2, prideBook;

    @BeforeEach
    void setUpGenresOverlapSimilarityCalculator() {
        String [] tokens1 = {"1", "Narnia1",
                "C.S. Lewis", "\"sth\"",
                "\"['Fantasy', 'Young Adult', 'Adventure', 'Childrens', 'Middle Grade']\"",
                "4.27", "617,006", "link1"};

        String [] tokens2 = {"2","Narnia2",
                "C.S. Lewis", "\"sth\"",
                "\"['Fantasy', 'Young Adult', 'Adventure', 'Fiction']\"",
                "4.23", "2,620,424", "link2"};

        String [] tokens3 = {"3", "Pride and Prejudice",
                "Jane Austen",
                "\" sth\"",
                "\"['Classics', 'Romance', 'Historical Fiction']\"",
                "4.28", "3,944,155", "link3"};

        narniaBook1 = Book.of(tokens1);
        narniaBook2 = Book.of(tokens2);
        prideBook = Book.of(tokens3);
        calculator = new GenresOverlapSimilarityCalculator();
    }

    @Test
    void testThrowsWhenFirstBookIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateSimilarity(null, narniaBook1));
    }

    @Test
    void testThrowsWhenSecondBookIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateSimilarity(narniaBook1, null));
    }

    @Test
    void testEmptyGenres() {
        String [] tokensNoGenres = {"1", "Narnia1",
                "C.S. Lewis", "\"sth\"",
                "\"[]\"", "4.27", "617,006", "link1"};
        Book bookNoGenres = Book.of(tokensNoGenres);
        System.out.println(calculator.calculateSimilarity(narniaBook1, bookNoGenres));
        assertEquals(calculator.calculateSimilarity(narniaBook1, bookNoGenres), 1.0);
    }

    @Test
    void testCalculateSimilarityOfSimilarGenres() {
        double commonElements = 3;
        double minSetSize = 4;
        assertEquals(calculator.calculateSimilarity(narniaBook1, narniaBook2),
                commonElements/minSetSize);

    }

    @Test
    void testCalculateSimilarityOfSameGenres() {
        assertEquals(calculator.calculateSimilarity(prideBook, prideBook), 1);
    }

    @Test
    void testCalculateSimilarityOfDifferentGenres() {
        assertEquals(calculator.calculateSimilarity(narniaBook1, prideBook), 0);
    }

}
