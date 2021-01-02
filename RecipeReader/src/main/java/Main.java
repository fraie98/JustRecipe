import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.google.gson.Gson;
import com.mongodb.client.*;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.neo4j.driver.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
        collection = database.getCollection("recipes");

        driver = GraphDatabase.driver( "neo4j://localhost:7687", AuthTokens.basic( "neo4j", "justrecipe" ) );

        collection.dropIndexes();

        // First of all It is useful to remove the old values (if they exists)
        collection.drop();
        deleteAllGraph();

        // Create the constraint (as index) on the tile of the recipe
        IndexOptions indexOptions = new IndexOptions().unique(true).name("title_constraint");
        collection.createIndex(Indexes.ascending("title"), indexOptions);
        // Create the constraint on the username of the User
        createUsernameConstraintNeo4j();
        createTitleConstraintNeo4j();

        List<RecipeRaw> rawRecipes = new ArrayList<>();
        addRecipes_full_format(rawRecipes, PATH_FULL_FORMAT_RECIPES);
        addRecipes_raw(rawRecipes, PATH_RECIPES_RAW_NOSOURCE_FN);

        //I have removed the last HOW_MANY_RECIPES_TO_REMOVE recipes, that are the ones that have not so much information
        rawRecipes.subList(rawRecipes.size() - HOW_MANY_RECIPES_TO_REMOVE, rawRecipes.size()).clear();

        // Remove the recipe with title == null (filter (...)) and remove the duplicates (distinct())
        List<RecipeRaw> recipesWithoutDuplicates = rawRecipes.stream().filter(new Predicate<RecipeRaw>() {
            @Override
            public boolean test(RecipeRaw recipeRaw) {
                if (recipeRaw.getTitle() != null)
                    return true;
                return false;
            }
        }).distinct().collect(Collectors.toList());

        // This reverse is used to insert the recipe with more information in the tail,
        // so they will be the last insert
        Collections.reverse(recipesWithoutDuplicates);

        // List of users present at the initial time of the application
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

        // First I insert the user, because when i choose randomly i can't be sure
        // that they will be all pick-up, so i need to be sure that they are all present
        // So, instead of adding the users in the insertRecipesAndUsers function, i have created another one function
        addUsers(users);
        insertRecipesOfUsers(recipesWithoutDuplicates, users);
        
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

    /**
     * Method that creates a new nodes in the graphDB with the information of the new users
     * @param users      Users to add
     */
    public static void addUsers( final List<User> users)
    {
        Map<String,Object> params = new HashMap<>();
        List<Map<String,Object>> list = new ArrayList<>();
        for (User user: users)
        {
            Map<String,Object> props = new HashMap<>();
            props.put( "firstName", user.getFirstName());
            props.put( "lastName", user.getLastName());
            props.put( "username", user.getUsername());
            props.put( "password", user.getPassword());
            list.add(props);
        }
        params.put( "batch", list );

        try ( Session session = driver.session())
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "UNWIND $batch AS row " +
                                "MERGE (u:User {firstName: row.firstName, lastName: row.lastName, " +
                                "username: row.username," +
                                "password: row.password, role:0})",
                        params);
                return null;
            });
        }
    }

    /**
     * Function that insert all the recipes, every one is associated at one user randomly picked
     * @param recipeRaws    The list of the recipes
     * @param users         The list of the users
     */
    public static void insertRecipesOfUsers (List<RecipeRaw> recipeRaws, List<User> users)
    {
        List<Document> documents = new ArrayList<Document>();
        Date date = new Date();
        Map<String,Object> params = new HashMap<>();
        List<Map<String,Object>> list = new ArrayList<>();
        Random random = new Random();
        int i= 0;
        for (RecipeRaw rawRecipe: recipeRaws) // For every recipe
        {
            // pick-up randomly a user to associate with this recipe
            int userIndex = random.nextInt(users.size()); //[0,19]
            User user = users.get(userIndex);

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
            doc.append("creationTime", new Date(date.getTime()+(1000*i)));
            doc.append("authorUsername", user.getUsername());
            documents.add(doc);

            // Neo4j part
            Map<String,Object> props = new HashMap<>();
            props.put( "username", user.getUsername());
            props.put("timestamp", new Date(date.getTime()+(1000*i)).getTime());
            props.put("title", rawRecipe.getTitle());
            if (rawRecipe.getCalories() != 0)
                props.put("calories", rawRecipe.getCalories());
            if (rawRecipe.getFat() != 0)
                props.put("fat", rawRecipe.getFat());
            if (rawRecipe.getProtein() != 0)
                props.put("protein", rawRecipe.getProtein());
            if (rawRecipe.getCarbs() != 0)
                props.put("carbs", rawRecipe.getCarbs());
            list.add(props);

            i++;
        }

        // Mongo insert
        collection.insertMany(documents);

        // Neo4j insert
        params.put( "batch", list );
        try ( Session session = driver.session())
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                // First step: find the right user
                // Second step: create the path ADDS and the recipe
                tx.run( "UNWIND $batch AS row " +
                                "MATCH (u:User {username: row.username}) " +
                                "CREATE (u)-[:ADDS {when: row.timestamp}]->(r:Recipe {title: row.title, calories: row.calories, " +
                                "fat: row.fat, protein: row.protein, carbs: row.carbs})",
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

    /**
     * This function creates the constraint on the username (that must be unique)
     */
    private static void createUsernameConstraintNeo4j() {
        try ( Session session = driver.session())
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "CREATE CONSTRAINT username_constraint IF NOT EXISTS ON (u: User) ASSERT u.username IS UNIQUE");
                return null;
            });
        }
    }

    /**
     * This function creates the constraint on the recipe title (that must be unique)
     */
    private static void createTitleConstraintNeo4j() {
        try ( Session session = driver.session())
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "CREATE CONSTRAINT title_constraint IF NOT EXISTS ON (r: Recipe) ASSERT r.title IS UNIQUE");
                return null;
            });
        }
    }

}
