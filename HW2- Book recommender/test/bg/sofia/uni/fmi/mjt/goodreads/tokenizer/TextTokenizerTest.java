package bg.sofia.uni.fmi.mjt.goodreads.tokenizer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import static org.junit.jupiter.api.Assertions.*;

public class TextTokenizerTest {

    private Set<String> stopWords;
    private TextTokenizer tokenizer;

    @BeforeEach
    void setUp() {
        stopWords = Set.of("a","one", "am", "are","and", "of",
                "the", "in","its", "has", "her",
                "as", "own", "most", "this");
        tokenizer = new TextTokenizer(stopWords);
    }

    @Test
    void testThrowsExceptionWhenStopWordsNull() {
        Set<String> nullStopWords = null;
        assertThrows(IllegalArgumentException.class,
                () -> new TextTokenizer(nullStopWords),
                "Tokenizer's constructor must throw " +
                        "IllegalArgumentException when stop words' set is null!");
    }

    @Test
    void testThrowsExceptionWhenStopWordsEmpty() {
        assertThrows(IllegalArgumentException.class,
                () -> new TextTokenizer(Collections.emptySet()),
                "Tokenizer's constructor must throw IllegalArgumentException for empty set of stop words!"
        );
    }

    @Test
    void testThrowExceptionWhenInputNull() {
        assertThrows(IllegalArgumentException.class,
                () -> tokenizer.tokenize(null),
                "Expected tokenize(null) to throw, but it didn't");
    }

    @Test
    void testThrowExceptionWhenInputStringEmpty() {
        assertThrows(IllegalArgumentException.class,
                () -> tokenizer.tokenize(""),
                "Expected tokenize(<empty string>) to throw, but it didn't");
    }

    @Test
    void testThrowExceptionWhenInputStringBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> tokenizer.tokenize("     "),
                "Expected tokenize(<blank string>) to throw, but it didn't");
    }

    @Test
    void testTokenizeInputWithStopWordsAndPunctuation() {
        String description = "Since its immediate success in 1813, Pride and Prejudice, " +
                "has remained one of the most popular novels in the English language. The " +
                "author- Jane Austen- called this brilliant work \"her own darling child\".";

        List<String> result =  tokenizer.tokenize(description);
        List<String> expectedResult = List.of("since", "immediate", "success",
                "1813", "pride", "prejudice","remained", "popular", "novels", "english",
                "language", "author","jane","austen", "called", "brilliant", "work",
                "darling", "child");
        assertEquals(result, expectedResult);
    }

    @Test
    void testTokenizeInputWithStopWordsOnly() {
        String description = "a of In AS her thIS THE most Its Own";
        List<String> result =  tokenizer.tokenize(description);
        assertTrue(result.isEmpty());
    }
}
