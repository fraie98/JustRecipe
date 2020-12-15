import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.mongodb.client.*;
import org.bson.Document;
import org.neo4j.driver.*;
import sun.tracing.dtrace.DTraceProviderFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.neo4j.driver.Values.parameters;

public class Main {
    public static int HOW_MANY_RECIPES_TO_REMOVE = 30000;
    public static String PATH_FULL_FORMAT_RECIPES = "C:/Users/danyc/Downloads/full_format_recipes/full_format_recipes.json";
    public static String PATH_RECIPES_RAW_NOSOURCE_FN = "C:/Users/danyc/Downloads/recipes_raw/recipes_raw_nosource_fn.json";

    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static MongoCollection collection;
    private static Driver driver;

    public static void main (String[] arg)
    {
        mongoClient = MongoClients.create();
        database = mongoClient.getDatabase("justrecipe");
        collection = database.getCollection("recipe");

        driver = GraphDatabase.driver( "neo4j://localhost:7687", AuthTokens.basic( "neo4j", "justrecipe" ) );

        // First of all It is useful to remove the old values (if they exists)
        collection.drop();
        deleteAllGraph();

        List<RecipeRaw> rawRecipes = new ArrayList<>();
        addRecipes_full_format(rawRecipes, PATH_FULL_FORMAT_RECIPES);
        addRecipes_raw(rawRecipes, PATH_RECIPES_RAW_NOSOURCE_FN);

        //I have removed the last HOW_MANY_RECIPES_TO_REMOVE recipes, that are the ones that have not so much information
        rawRecipes.subList(rawRecipes.size() - HOW_MANY_RECIPES_TO_REMOVE, rawRecipes.size()).clear();

        // Remove the recipe with title == null (the filter (...)) and remove the duplicates (distinct())
        List<RecipeRaw> recipesWithoutDuplicates = rawRecipes.stream().filter(new Predicate<RecipeRaw>() {
            @Override
            public boolean test(RecipeRaw recipeRaw) {
                if (recipeRaw.getTitle() != null)
                    return true;
                return false;
            }
        }).distinct().collect(Collectors.toList());

        List<User> users = new ArrayList<>();
        users.add(new User("Oliver", "Smith", "oliver.smith", "oliver.smith"));
        users.add(new User("Jack", "Jones", "jack.jones", "jack.jones"));
        users.add(new User("Harry", "Williams", "harry.williams", "harry.williams"));
        users.add(new User("Jacob", "Brown", "jacob.brown", "jacob.brown"));
        users.add(new User("Charlie", "Taylor", "charlie.taylor", "charlie.taylor"));
        users.add(new User("Thomas", "Davies", "thomas.davies", "thomas.davies"));
        users.add(new User("George", "Wilson", "george.wilson", "george.wilson"));
        users.add(new User("Oscar", "Evans", "oscar.evans", "oscar.evans"));
        users.add(new User("James", "Thomas", "james.thomas", "james.thomas"));
        users.add(new User("William", "Roberts", "william.roberts", "william.roberts"));
        users.add(new User("Amelia", "Murphy", "amelia.murphy", "amelia.murphy"));
        users.add(new User("Olivia", "Johnson", "olivia.johnson", "olivia.johnson"));
        users.add(new User("Isla", "Williams", "isla.williams", "isla.williams"));
        users.add(new User("Emily", "Walsh", "emily.walsh", "emily.walsh"));
        users.add(new User("Poppy", "Taylor", "poppy.taylor", "poppy.taylor"));
        users.add(new User("Ava", "Miller", "ava.miller", "ava.miller"));
        users.add(new User("Isabella", "Byrne", "isabella.byrne", "isabella.byrne"));
        users.add(new User("Jessica", "Evans", "jessica.evans", "jessica.evans"));
        users.add(new User("Lily", "Rodriguez", "lily.rodriguez", "lily.rodriguez"));
        users.add(new User("Sophie", "Roberts", "sophie.roberts", "sophie.roberts"));
        addRecipesToDatabases(recipesWithoutDuplicates, users);
        
        //collection.insertMany(documents);
        System.out.println(collection.countDocuments()); //How many documents loaded
        mongoClient.close();
        driver.close();
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

    public static void addRecipesToDatabases (List<RecipeRaw> recipeRawList, List<User> users)
    {
        // Partition the recipe in "users.size()" sublist
        List<List<RecipeRaw>> lists = new ArrayList<List<RecipeRaw>>(users.size());
        // Initialization of each sublist
        for (int i = 0; i < users.size(); i++) {
            lists.add(new ArrayList<RecipeRaw>());
        }
        // Inserting the values of the original list in the sublists, O(n)
        // The first in the first, the second in the second, and so on
        // With the module I come back to 0 after I reach users.size()
        int index = 0;
        for (RecipeRaw t : recipeRawList) {
            lists.get(index).add(t);
            index = (index + 1) % users.size();
        }
        // Insert every sublist with the user in the databases
        int i = 0;
        for (List<RecipeRaw> recipeRaws: lists)
        {
            insertRecipesOfUser(recipeRaws, users.get(i));
            i++;
        }
    }

    public static void insertRecipesOfUser (List<RecipeRaw> recipeRaws, User user)
    {
        List<Document> documents = new ArrayList<Document>();
        Date date = new Date();
        Map<String,Object> params = new HashMap<>();
        List<Map<String,Object>> list = new ArrayList<>();
        for (RecipeRaw rawRecipe: recipeRaws) // For every recipe to associate with this user
        {
            // MongoDB part
            String title = rawRecipe.getTitle();
            Document doc = new Document("title", title);
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
            doc.append("authorUsername", user.getUsername());
            documents.add(doc);

            // Neo4j part
            Map<String,Object> props = new HashMap<>();
            props.put( "firstName", user.getFirstName());
            props.put( "lastName", user.getLastName());
            props.put( "username", user.getUsername());
            props.put( "password", user.getPassword());
            props.put("title", rawRecipe.getTitle());
            list.add(props);
        }

        // Mongo insert
        collection.insertMany(documents);

        // Neo4j insert
        params.put( "batch", list );
        try ( Session session = driver.session())
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                // First step: find the user with the merge (if he doesn't exist, he will be created)
                // Second step: merge the path, for creating it if it is necessary
                tx.run( "UNWIND $batch AS row " +
                                "MERGE (u:User {firstName: row.firstName, lastName: row.lastName, " +
                                "username: row.username, password: row.password}) " +
                                "MERGE (u)-[:ADD]->(r:Recipe {title: row.title})",
                        params);
                return null;
            });
        }
    }

    /**
     * Function used to delete all the nodes and the edges of the graph
     */
    public static void deleteAllGraph ()
    {
        try ( Session session = driver.session())
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "MATCH (n) DETACH DELETE n");
                return null;
            });
        }
    }

}
