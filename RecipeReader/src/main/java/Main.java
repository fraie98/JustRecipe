import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.google.gson.Gson;
import com.mongodb.client.*;
import org.bson.Document;
import java.io.IOException;
import java.nio.file.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static int HOW_MANY_RECIPES_TO_REMOVE = 30000;
    public static String PATH_FULL_FORMAT_RECIPES = "C:/Users/danyc/Downloads/full_format_recipes/full_format_recipes.json";
    public static String PATH_RECIPES_RAW_NOSOURCE_FN = "C:/Users/danyc/Downloads/recipes_raw/recipes_raw_nosource_fn.json";

    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static MongoCollection collection;

    public static void main (String[] arg)
    {
        mongoClient = MongoClients.create();
        database = mongoClient.getDatabase("justrecipe");
        collection = database.getCollection("recipe");

        //List of documents to insert in MongoDB
        List<Document> documents = new ArrayList<Document>();
        List<RecipeRaw> rawRecipes = new ArrayList<>();
        addRecipes_full_format(rawRecipes, PATH_FULL_FORMAT_RECIPES);
        addRecipes_raw(rawRecipes, PATH_RECIPES_RAW_NOSOURCE_FN);

        //I have removed the last HOW_MANY_RECIPES_TO_REMOVE recipes, that are the ones that have not so much information
        rawRecipes.subList(rawRecipes.size() - HOW_MANY_RECIPES_TO_REMOVE, rawRecipes.size()).clear();
        List<RecipeRaw> recipesWithoutDuplicates = rawRecipes.stream().distinct().collect(Collectors.toList());

        Date date = new Date();

        for (RecipeRaw rawRecipe: recipesWithoutDuplicates)
        {
            Document doc = new Document("title", rawRecipe.getTitle());
            if (rawRecipe.getInstructions() != null)
            {
                doc.append("instructions", rawRecipe.getInstructions());
            }
            else if (rawRecipe.getDirections() != null) // this recipe has the directions
            {
                String instructions = "";
                for (String s : rawRecipe.getDirections())
                {
                    instructions += s + "\n";
                }
                doc.append("instructions", instructions);
            }
            if (rawRecipe.getIngredients() != null)
                doc.append("ingredients", rawRecipe.getIngredients());
            if (rawRecipe.getCategories() != null)
                doc.append("categories", rawRecipe.getCategories());
            if (rawRecipe.getCalories() != 0)
                doc.append("calories", rawRecipe.getCalories());
            if (rawRecipe.getFat() != 0)
                doc.append("fat", rawRecipe.getFat());
            if (rawRecipe.getProtein() != 0)
                doc.append("protein", rawRecipe.getProtein());
            if (rawRecipe.getCarbs() != 0)
                doc.append("carbs", rawRecipe.getCarbs());
            // For the timestamp MongoDB use the "Date"
            doc.append("creationTime", date);
            doc.append("author", "admin");
            documents.add(doc);
        }
        
        collection.insertMany(documents);
        System.out.println(collection.countDocuments()); //How many documents loaded
        mongoClient.close();
    }

    /**
     * Add recipes from raw dataset
     * @param recipes
     * @param path
     */
    public static void addRecipes_raw (List<RecipeRaw> recipes, String path)
    {
        Gson gson = new Gson();
        JsonFactory factory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper(factory);
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readTree(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Iterator<Map.Entry<String,JsonNode>> fieldsIterator = rootNode.fields();
        while (fieldsIterator.hasNext()) {
            Map.Entry<String,JsonNode> field = fieldsIterator.next();
            //The key is the unknow ID, the value is the recipe
            recipes.add(gson.fromJson(field.getValue().toString(), RecipeRaw.class));
        }
    }

    /**
     * Add recipes from full_format dataset
     * @param recipes
     * @param path
     */
    public static void addRecipes_full_format (List<RecipeRaw> recipes, String path)
    {
        Gson gson = new Gson();
        RecipeRaw[] recipeList = null;
        StringBuilder contentBuilder = new StringBuilder();
        try {
            Files.lines(Paths.get(path)).forEach(s -> contentBuilder.append(s));
            recipeList = gson.fromJson(contentBuilder.toString(), RecipeRaw[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.addAll(recipes, recipeList);
    }
}
