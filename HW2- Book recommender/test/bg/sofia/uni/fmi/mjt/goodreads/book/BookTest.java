package bg.sofia.uni.fmi.mjt.goodreads.book;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BookTest {

    @Test
    void testBookCreation() {
        List<String> genres = List.of("Classics", "Fiction", "Historical Fiction", "Young Adult");

        Book book = new Book("0", "To Kill a Mockingbird", "Harper Lee",
                "The unforgettable novel of a childhood in a " +
                        "sleepy Southern town.",
                genres,4.27 ,5691311,
                "link1");

        assertEquals("0", book.ID());
        assertEquals("To Kill a Mockingbird", book.title());
        assertEquals("Harper Lee", book.author());
        assertEquals("The unforgettable novel of a childhood in a " +
                        "sleepy Southern town.", book.description());
        assertEquals(genres, book.genres());
        assertEquals(4.27, book.rating());
        assertEquals(5691311, book.ratingCount());
        assertEquals("link1", book.URL());
    }

    @Test
    void testBookOfValidInput() {
        String [] tokens = {"0", "To Kill a Mockingbird",
                "Harper Lee", "\"The unforgettable novel of " +
                "a childhood in a sleepy Southern town.\"",
                "\"['Classics', 'Fiction', 'Historical Fiction', 'School', " +
                        "'Literature','Young Adult', 'Historical']\"",
                "4.27" ,"5,691,311",
                "link1" };

        Book book = Book.of(tokens);
        List<String> genres = List.of("Classics", "Fiction", "Historical Fiction",
                "School", "Literature", "Young Adult", "Historical");

        assertEquals("0", book.ID());
        assertEquals("To Kill a Mockingbird", book.title());
        assertEquals("Harper Lee", book.author());
        assertEquals("The unforgettable novel of a childhood in a " +
                "sleepy Southern town.", book.description());
        assertEquals(genres, book.genres());
        assertEquals(4.27, book.rating());
        assertEquals(5691311, book.ratingCount());
        assertEquals("link1", book.URL());
    }

    @Test
    void testBookOfInvalidInputLength() {     //must add other tests as well
        String [] invalidTokens = {"0", "To Kill a Mockingbird",
                "Harper Lee"};
        assertThrows(IllegalArgumentException.class,
                () -> Book.of(invalidTokens));
    }

    @Test
    void testParseGenresCorrectInput() {
        String genresString = "\"['Fiction', 'Dystopia', " +
                "'Fantasy', 'Politics', 'School', 'Literature']\"";
        List<String> expectedGenres = List.of("Fiction",
                "Dystopia", "Fantasy","Politics", "School",
                "Literature");

        assertEquals(expectedGenres, Book.parseGenres(genresString));
    }

    @Test
    void testParseGenresNullInput() {
       assertThrows(IllegalArgumentException.class, () -> Book.parseGenres(null),
               "Expected exception when string of genres is null!");
    }

    @Test
    void testParseGenresEmptyInput() {
        assertThrows(IllegalArgumentException.class, () -> Book.parseGenres(""),
                "Expected exception when string of genres is null!");
    }

    @Test
    void testParseGenresBlankInput() {
        assertThrows(IllegalArgumentException.class, () -> Book.parseGenres("  "),
                "Expected exception when string of genres is null!");
    }

    @Test
    void testParseGenresEmptyList() {
        assertTrue(Book.parseGenres("\"[]\"").isEmpty());
    }
}
