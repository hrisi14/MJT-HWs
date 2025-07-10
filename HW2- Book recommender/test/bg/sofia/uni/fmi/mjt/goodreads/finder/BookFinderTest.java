package bg.sofia.uni.fmi.mjt.goodreads.finder;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.tokenizer.TextTokenizer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BookFinderTest {

    private static final String [] tokens1 = {"1", "The Chronicles of Narnia",
                "C.S. Lewis", "\"Journeys, end of the world, fantastic creatures, and epic battles " +
                "between good and evil. The Lion, the Witch and the Wardrobe, by Clive Staples Lewis." +
                "The Chronicles of Narnia.\"", "\"['Fantasy', 'Classics', 'Young Adult', 'Adventure']\"",
            "4.27", "617,006", "link1" };

    private static final String [] tokens2 = {"2","The Lion, the Witch and the Wardrobe",
                "C.S. Lewis", "\"Narnia… the land wardrobe door, a secret place frozen in eternal winter, " +
                "a magical country waiting to be set free. In Narnia country evil enchantment of the White " +
                "Witch and then meet the Lion Aslan.\"",
                "\"['Fantasy', 'Classics', 'Fiction', 'Young Adult', 'Childrens', " +
                "'Middle Grade', 'Adventure']\"", "4.23", "2,620,424", "link2"};

    private static final String [] tokens3 = {"3", "Pride and Prejudice", "Jane Austen",
                "\"Pride and Prejudice, English language, Jane Austen, brilliant work. The " +
                        "romantic clash, flirtation and intrigue, Regency England.\"",
                "\"['Classics', 'Fiction', 'Romance', 'Historical Fiction']\"",
            "4.28", "3,944,155", "link3"};

    private Set<Book> books;
    private TextTokenizer tokenizer;
    private BookFinder bookFinder;
    private Book book1, book2, book3;

    @BeforeEach
    void setUpFinder() {
        book1 = Book.of(tokens1);
        book2 = Book.of(tokens2);
        book3 = Book.of(tokens3);
        books = Set.of(book1, book2, book3);

        Set<String> stopWords = Set.of("a","one", "am", "are","and", "of",
                "the", "in","its", "has", "her",
                "as", "own", "most", "this");
        tokenizer = new TextTokenizer(stopWords);
        bookFinder = new BookFinder(books, tokenizer);
    }

    @Test
    void testSearchByAuthorThrowsWhenAuthorNull() {
        assertThrows(IllegalArgumentException.class,
                () -> bookFinder.searchByAuthor(null),
                "Exception expected in case searched " +
                        "author is null!");
    }

    @Test
    void testSearchByAuthorThrowsWhenAuthorEmpty() {
        assertThrows(IllegalArgumentException.class,
                () -> bookFinder.searchByAuthor(""),
                "Exception expected in case searched " +
                        "author is an empty string!");
    }

    @Test
    void testSearchByAuthorThrowsWhenAuthorBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> bookFinder.searchByAuthor("   "),
                "Exception expected in case searched " +
                        "author is a blank string!");
    }

    @Test
    void testSearchByAuthor() {
       assertEquals("Pride and Prejudice", bookFinder.
               searchByAuthor("Jane Austen").getFirst().title());
    }

    @Test
    void testSearchByAuthorNoSuchAuthor() {
        assertTrue(bookFinder.searchByAuthor("David Beckham").isEmpty());
    }

    @Test
    void testSearchByGenresThrowsWhenNullGenres() {
        assertThrows(IllegalArgumentException.class,
                () -> bookFinder.searchByGenres(null, MatchOption.MATCH_ALL),
                "Exception expected in case searched genres are null!");
    }

    @Test
    void testSearchByGenresThrowsWhenEmptySetOfGenres() {
        assertThrows(IllegalArgumentException.class,
                () -> bookFinder.searchByGenres(new HashSet<String>(), MatchOption.MATCH_ALL),
                "Exception expected in case searched genres are null!");
    }

    @Test
    void testSearchByGenresMatchAll() {
        Set<String> genres = Set.of("Young Adult", "Fantasy", "Adventure");
        List<Book> expectedResult = List.of(book1, book2);

        assertTrue(bookFinder.searchByGenres(genres,
                MatchOption.MATCH_ALL).containsAll(expectedResult));
    }

    @Test
    void testSearchByGenresMatchAny() {
        Set<String> genres = Set.of("Romance", "Fantasy", "Classics");
        List<Book> expectedResult = List.of(book1, book2, book3);

        assertTrue(bookFinder.searchByGenres(genres,
                MatchOption.MATCH_ANY).containsAll(expectedResult));
    }

    @Test
    void testSearchByKeywordsThrowsWhenNullKeywords() {
        assertThrows(IllegalArgumentException.class,
                () -> bookFinder.searchByKeywords(null, MatchOption.MATCH_ALL),
                "Exception expected in case searched keywords are null!");
    }

    @Test
    void testSearchByKeywordsThrowsWhenEmptySetOfKeywords() {
        assertThrows(IllegalArgumentException.class,
                () -> bookFinder.searchByKeywords(new HashSet<String>(), MatchOption.MATCH_ALL),
                "Exception expected in case searched genres are null!");
    }

    @Test
    void testSearchByKeywordsMatchAll() {
        Set<String> keyWords = Set.of("Narnia", "evil", "WITCH");
        List<Book> expected = List.of(book1, book2);

        assertEquals(bookFinder.searchByKeywords(keyWords,
                MatchOption.MATCH_ALL), expected);
    }

    @Test
    void testSearchByKeywordsMatchAllInTitleOrDescription() {
        Set<String> keyWords = Set.of("prejudice", "romantic", "flirtation"); //the first word is
        List<Book> expected = List.of(book3);   // in the title, the other - in descr

        assertEquals(bookFinder.searchByKeywords(keyWords,
                MatchOption.MATCH_ALL), expected);
    }

    @Test
    void testSearchByKeywordsMatchAny() {
        Set<String> keyWords = Set.of("Narnia", "evil", "romantic");
        List<Book> expected = List.of(book1, book2, book3);

        assertTrue(bookFinder.searchByKeywords(keyWords,
                MatchOption.MATCH_ANY).containsAll(expected));
    }

    @Test
    void testSearchByKeywordsMatchAllNoMatch() {
        Set<String> keyWords = Set.of("Narnia", "witch", "horror");

        assertTrue(bookFinder.searchByKeywords(keyWords,
                MatchOption.MATCH_ALL).isEmpty());
    }

    @Test
    void testSearchByKeywordsMatchAnyNoMatch() {
        Set<String> keyWords = Set.of("horror", "scary");

        assertTrue(bookFinder.searchByKeywords(keyWords,
                MatchOption.MATCH_ANY).isEmpty());
    }

    @Test
    void testGetAllGenres() {
        Set<String> expectedResult = Set.of("Classics", "Fiction", "Romance",
                "Fantasy", "Young Adult", "Adventure",
                "Middle Grade", "Childrens", "Historical Fiction");

        assertEquals(bookFinder.allGenres(), expectedResult);
    }

    @Test
    void testGetAllBooks() {
        Set<Book> expectedResult = Set.of(book1, book2, book3);
        assertEquals(bookFinder.allBooks(), expectedResult);
    }
}
