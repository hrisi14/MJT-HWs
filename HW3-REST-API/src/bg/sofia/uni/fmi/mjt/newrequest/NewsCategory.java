package bg.sofia.uni.fmi.mjt.newrequest;

public enum NewsCategory {
    DEFAULT(""),
    BUSINESS("business"),
    ENTERTAINMENT("entertainment"),
    GENERAL("general"),
    HEALTH("health"),
    SCIENCE("science"),
    SPORTS("sports"),
    TECHNOLOGY("technology");

    private final String categoryName;

    NewsCategory(String name) {
        this.categoryName = name;
    }

    public String category() {
        return categoryName;
    }
}