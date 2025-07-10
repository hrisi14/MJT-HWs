package bg.sofia.uni.fmi.mjt.goodreads;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import org.junit.jupiter.api.Test;
import java.io.Reader;
import java.io.StringReader;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BookLoaderTest {

    private static final String CSV_BOOKS_INPUT = "N,Book,Author,Description,Genres," +
            "Avg_Rating,Num_Ratings,URL\n" +
            "0,To Kill a Mockingbird,Harper " +
            "Lee,\"unforgettable novel\",\"['Classics', 'Fiction']\"," +
            "4.27,\"5,691,311\",link1";

    private BookLoader loader;

    @Test
    void testLoadBookSet() {
        String [] tokens = {"0", "To Kill a Mockingbird", "Harper Lee", "unforgettable novel",
        "\"['Classics', 'Fiction']\"", "4.27", "5,691,311", "link1"};
        Book expectedBook = Book.of(tokens);
        Set<Book> expSet = Set.of(expectedBook);

        Reader reader = new StringReader(CSV_BOOKS_INPUT);
        assertEquals(BookLoader.load(reader), expSet);
    }

    @Test
    void testThrowsException() {
        Reader invalidR = new StringReader("invalid,data\n1,2,3");
        assertThrows(IllegalArgumentException.class, () -> BookLoader.load(invalidR));
    }
}
