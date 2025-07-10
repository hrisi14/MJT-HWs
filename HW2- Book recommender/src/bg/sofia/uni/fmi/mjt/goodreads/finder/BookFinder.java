package bg.sofia.uni.fmi.mjt.goodreads.finder;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.tokenizer.TextTokenizer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BookFinder implements BookFinderAPI {

   //to check the order of data members!!!

    private Set<Book> books; //is this the best collection???
    private TextTokenizer tokenizer;

    public BookFinder(Set<Book> books, TextTokenizer tokenizer) {
        if (books == null || books.isEmpty() || tokenizer == null) {
            throw new IllegalArgumentException("Invalid data has been " +
                    "passed to BookFinder's constructor!");
        }
        this.books = new HashSet<>(books);
        this.tokenizer = tokenizer;
    }

    public Set<Book> allBooks() {
        if (books == null || books.isEmpty()) {
            throw new IllegalArgumentException("Book list is empty!");
        }
        return books;
    }

    @Override
    public List<Book> searchByAuthor(String authorName) {
        if (authorName == null || authorName.isEmpty() ||
                authorName.isBlank()) {
            throw new IllegalArgumentException("Author's name must not be null or blank!");
        }
        return books.stream()
                .filter(book -> book.author().equals(authorName)).toList();
    }

    @Override
    public Set<String> allGenres() {
        if (books == null || books.isEmpty()) {
            throw new IllegalArgumentException("Book list is empty!");
        }
        return books.stream().map(Book::genres).flatMap(List::stream).collect(Collectors.toSet());
    }

    @Override
    public List<Book> searchByGenres(Set<String> genres, MatchOption option) {
        if (genres == null || genres.isEmpty() || genres.contains("")) {
            throw new IllegalArgumentException("Set of genres can not " +
                    "be null/empty or contain empty data!");
        }

        if (option != MatchOption.MATCH_ANY &&
                option != MatchOption.MATCH_ALL) {
            throw new IllegalArgumentException("Invalid match option!");
        }

        if (option == MatchOption.MATCH_ALL) {
            return books.stream().filter(book ->
                    new HashSet<>(book.genres()).containsAll(genres)).toList();  //???
        } else {
            return books.stream().filter(book ->
                   genres.stream().anyMatch(book.genres()::contains)).toList();
        }
    }


    /**
     * Searches for books that match the specified keywords.
     * The search can be based on different match options (all or any keywords).
     *
     * @param keywords a {@code Set} of keywords to search for.
     * @param option the {@code MatchOption} that defines the search criteria
     *               (either {@link MatchOption#MATCH_ALL} or {@link MatchOption#MATCH_ANY}).
     * @return a List of books in which the title or description match the given keywords according to the MatchOption
     *         Returns an empty list if no books are found.
     */
    @Override
    public List<Book> searchByKeywords(Set<String> keywords, MatchOption option) {  //To test this is an obligation!!!
        if (keywords == null || keywords.isEmpty()) {
            throw new IllegalArgumentException("Set of keywords can not be null or empty!");
        }
        if (option != MatchOption.MATCH_ANY &&
                option != MatchOption.MATCH_ALL) {
            throw new IllegalArgumentException("Invalid match option!");
        }

        Set<String> lowerCaseKeywords = keywords.stream()   //just in case, I do not know if it's actually needed
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        return books.stream().filter(book -> {
            Set<String> tokenizedDescription = new HashSet<>(tokenizer.tokenize(book.description()));
            Set<String> tokenizedTitle = new HashSet<>(tokenizer.tokenize(book.title()));

            if (option == MatchOption.MATCH_ALL) {
                return lowerCaseKeywords.stream().allMatch(keyword ->
                        tokenizedTitle.contains(keyword) ||
                                tokenizedDescription.contains(keyword));
            } else {
                return lowerCaseKeywords.stream().anyMatch(tokenizedTitle::contains) ||
                        keywords.stream().anyMatch(tokenizedDescription::contains);
            }
        } ).toList();
    }
}
