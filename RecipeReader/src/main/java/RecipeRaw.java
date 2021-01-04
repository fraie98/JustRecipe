import java.util.*;

/**
 * In this class there are the attribute in common and even those present only in a dataset
 */
public class RecipeRaw {
    private String instructions;
    private List<String> directions;
    private List<String> ingredients;
    private String title;
    private int calories;
    private int fat;
    private int protein;
    private int carbs;
    private List<String> categories;

    public RecipeRaw() {}

    /**
     *
     * @param o
     * @return true if the recipes are equal (same title if different object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecipeRaw recipeRaw = (RecipeRaw) o;

        return title != null ? title.equals(recipeRaw.title) : recipeRaw.title == null;
    }

    @Override
    public int hashCode() {
        return title != null ? title.hashCode() : 0;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public int getCalories() {
        return calories;
    }

    public String getInstructions() {
        return instructions;
    }

    public List<String> getDirections() {
        return directions;
    }

    public String getTitle() {
        return title;
    }

    public int getFat() {
        return fat;
    }

    public int getProtein() {
        return protein;
    }

    public int getCarbs() {
        return carbs;
    }

    public List<String> getCategories() {
        return categories;
    }
}
