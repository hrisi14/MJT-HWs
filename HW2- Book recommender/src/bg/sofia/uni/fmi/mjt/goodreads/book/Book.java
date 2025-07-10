package bg.sofia.uni.fmi.mjt.goodreads.book;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public record Book(
        String ID,
        String title,
        String author,
        String description,
        List<String> genres,
        double rating,
        int ratingCount,
        String URL
) {
    private static final short FIRST_INDEX = 0;
    private static final short SECOND_INDEX = 1;
    private static final short THIRD_INDEX = 2;
    private static final short FOURTH_INDEX = 3;
    private static final short FIFTH_INDEX = 4;
    private static final short SIXTH_INDEX = 5;
    private static final short SEVENTH_INDEX = 6;
    private static final short EIGHT_INDEX = 7;
    private static final short TOTAL_COUNT = 8;

    public static Book of(String[] tokens) {
        if (tokens.length != TOTAL_COUNT) {
            throw new IllegalArgumentException("Invalid number of book's fields!");
        }
        String parsedDescription =
                tokens[FOURTH_INDEX].trim().substring(1, tokens[FOURTH_INDEX].length() - 1);
        try {
            return new Book(
                    tokens[FIRST_INDEX].trim(),
                    tokens[SECOND_INDEX].trim(),
                    tokens[THIRD_INDEX].trim(),
                    parsedDescription,
                    parseGenres(tokens[FIFTH_INDEX]),
                    Double.parseDouble(tokens[SIXTH_INDEX].trim()),
                    Integer.parseInt(tokens[SEVENTH_INDEX].replace(",", "")),
                    tokens[EIGHT_INDEX].trim()
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format in an input field!", e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Input line is in invalid format!", e);
        }
    }

    static List<String> parseGenres(String genres) {
        if (genres == null || genres.isEmpty() || genres.isBlank()) {
            throw new IllegalArgumentException("Invalid genres input!");
        }

        if (genres.matches("\"\\[\s*\\]\"")) {  //empty list of genres
            return new LinkedList<>();
        }

        String [] words = genres.substring(2, genres.length() - 2).split(",");
        List<String> resultGenres = new ArrayList<>();
        for (String genre: words) {
            resultGenres.add(genre.trim().replaceAll("'", ""));
        }
        return resultGenres;
    }
}
