package it.unipi.dii.inginf.lsdb.justrecipe.persistence;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import it.unipi.dii.inginf.lsdb.justrecipe.config.ConfigurationParameters;
import it.unipi.dii.inginf.lsdb.justrecipe.model.*;
import it.unipi.dii.inginf.lsdb.justrecipe.utils.Utils;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Sorts.descending;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * This class is used to communicate with MongoDB
 */
public class MongoDBDriver implements DatabaseDriver{
    private static MongoDBDriver instance;

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection collection;
    private CodecRegistry pojoCodecRegistry;
    private String ip;
    private int port;
    private String username;
    private String password;
    private String dbName;

    public static MongoDBDriver getInstance() {
        if (instance == null)
        {
            instance = new MongoDBDriver(Utils.readConfigurationParameters());
        }
        return instance;
    }

    /**
     * Consumer function that prints the document in json format
     */
    private Consumer<Document> printDocuments = doc -> {
        System.out.println(doc.toJson());
    };

    private MongoDBDriver (ConfigurationParameters configurationParameters)
    {
        this.ip = configurationParameters.getMongoIp();
        this.port = configurationParameters.getMongoPort();
        this.username = configurationParameters.getMongoUsername();
        this.password = configurationParameters.getMongoPassword();
        this.dbName = configurationParameters.getMongoDbName();
        initConnection();
        chooseCollection("recipe");
    }

    /**
     * Method that inits the MongoClient and choose the correct database
     */
    @Override
    public void initConnection() {
        ConnectionString connectionString;
        if (!username.equals("")) // if there are access rules
        {
            connectionString = new ConnectionString("mongodb://" + username + ":" + password
                    + "@" + ip + ":" + port);
        }
        else // standard access
        {
            connectionString = new ConnectionString("mongodb://" + ip + ":" + port);
        }
        mongoClient = MongoClients.create(connectionString);
        database = mongoClient.getDatabase(dbName);
        pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
    }

    /**
     * Method used to close the connection
     */
    @Override
    public void closeConnection() {
        if (mongoClient != null)
            mongoClient.close();
    }

    /**
     * Add a new recipe in MongoDB
     * @param r The object Recipe which contains all the necessary information about it
     */
    public void addRecipe(Recipe r)
    {
        Document doc = new Document("title",r.getTitle())
                .append("instructions",r.getInstructions())
                .append("ingredients",r.getIngredients());
        // Optional fields
        if(!r.getCategories().isEmpty())
            doc.append("categories",r.getCategories());
        if(r.getCalories()!=-1)
            doc.append("calories",r.getCalories());
        if(r.getFat()!=-1)
            doc.append("fat",r.getFat());
        if(r.getProtein()!=-1)
            doc.append("protein",r.getProtein());
        if(r.getCarbs()!=-1)
            doc.append("carbs",r.getCarbs());
        // Automatic fields
        doc.append("creationTime",new Date(r.getCreationTime().getTime()))
                .append("authorUsername",r.getAuthorUsername());
        // Other option field
        if(!r.getPicture().isEmpty())
            doc.append("picture",r.getPicture());

        collection.insertOne(doc);
    }
    /**
     * Method used to change the collection
     * @param name  name of the new collection
     */
    public void chooseCollection(String name)
    {
        collection = database.getCollection(name);
    }

    public List<Recipe> getRecipesFromAuthorUsername(int howManySkip, int howMany, String username){
        List<Recipe> recipes = new ArrayList<>();
        Gson gson = new Gson();
        List<Document> results = new ArrayList<>();
        Bson sort = sort(descending("creationTime"));
        Bson skip = skip(howManySkip);
        Bson limit = limit(howMany);
        Bson match = match(eq("authorUsername", username));
        results = (List<Document>) collection.aggregate(Arrays.asList(match, sort, skip, limit))
                .into(new ArrayList<>());
        Type recipeListType = new TypeToken<ArrayList<Recipe>>(){}.getType();
        recipes = gson.fromJson(gson.toJson(results), recipeListType);
        return recipes;
    }

    /**
     * Function that return the recipe given the title
     * @param title     Title of the recipe
     * @return          The recipe
     */
    public Recipe getRecipeFromTitle(String title){
        Recipe recipe = null;
        Gson gson = new Gson();
        Type recipeType = new TypeToken<Recipe>(){}.getType();
        Document myDoc =(Document) collection.find(eq("title", title)).first();
        recipe = gson.fromJson(gson.toJson(myDoc), recipeType);
        return recipe;
    }

    /**
     * Function that returns "howMany" recipes that contains in their title the title inserted by the user
     * @param title         Title to check
     * @param howManySkip   How many to skip
     * @param howMany       How many recipe we want obtain
     * @return              The list of the recipes that match the condition
     */
    public List<Recipe> searchRecipesFromTitle (String title, int howManySkip, int howMany)
    {
        List<Recipe> recipes = new ArrayList<>();
        Gson gson = new Gson();
        Pattern pattern = Pattern.compile("^.*" + title + ".*$", Pattern.CASE_INSENSITIVE);
        Bson match = Aggregates.match(Filters.regex("title", pattern));
        Bson sort = sort(descending("creationTime"));
        Bson skip = skip(howManySkip);
        Bson limit = limit(howMany);
        List<Document> results = (List<Document>) collection.aggregate(Arrays.asList(match, sort, skip, limit))
                .into(new ArrayList<>());
        Type recipeListType = new TypeToken<ArrayList<Recipe>>(){}.getType();
        recipes = gson.fromJson(gson.toJson(results), recipeListType);
        return recipes;
    }

    /**
     * Function that return the most common categories (the top one used)
     * @param howManySkip           How many to skip
     * @param howManyCategories     How many category to consider in the rank
     * @return                      The category ordered by the number of recipes in which it is used
     */
    public List<String> searchMostCommonRecipeCategories (int howManySkip, int howManyCategories)
    {
        List<String> mostCommonCategories = new ArrayList<>();
        Bson unwind = unwind("$categories");
        Bson group = group("$categories", Accumulators.sum("numberOfRecipes", 1));
        Bson project = project(fields(computed("categories", "$_id"), excludeId(), include("numberOfRecipes")));
        Bson sort = sort(descending("numberOfRecipes"));
        Bson skip = skip(howManySkip);
        Bson limit = limit(howManyCategories);
        List<Document> results = (List<Document>)
                collection.aggregate(Arrays.asList(unwind, group, project, sort, skip, limit)).into(new ArrayList());

        for (Document document: results)
        {
            mostCommonCategories.add(document.getString("categories"));
        }
        return mostCommonCategories;
    }

    /**
     * Function that returns "howMany" recipes of one category
     * @param category      The category to consider
     * @param howMany       How many recipes to return
     * @return              List of the recipes
     */
    public List<Recipe> getRecipesOfCategory (String category, int howMany)
    {
        List<Recipe> recipes = new ArrayList<>();
        Gson gson = new Gson();
        Bson match = match(Filters.in("categories", category));
        Bson sort = sort(descending("creationTime"));
        Bson limit = limit(howMany);
        List<Document> results = (List<Document>)
                collection.aggregate(Arrays.asList(match, sort, limit)).into(new ArrayList());
        Type recipeListType = new TypeToken<ArrayList<Recipe>>(){}.getType();
        recipes = gson.fromJson(gson.toJson(results), recipeListType);
        return recipes;
    }

    /**
     * Function for searching all the comments, ordered by the creationTime (first the last)
     * The list will be useful for the moderators
     * @param howManySkip   How many to skip
     * @param howMany       How many comments we want obtain
     * @return              The list of the comments
     */
    public List<Comment> searchAllComments (int howManySkip, int howMany)
    {
        List<Comment> comments = new ArrayList<>();
        Gson gson = new Gson();
        Bson unwind = unwind("$comments");
        Bson sort = sort(descending("creationTime"));
        Bson skip = skip(howManySkip);
        Bson limit = limit(howMany);
        MongoCursor<Document> iterator = (MongoCursor<Document>)
                collection.aggregate(Arrays.asList(unwind, sort, skip, limit)).iterator();
        while (iterator.hasNext())
        {
            Document document = (Document) iterator.next().get("comments");
            Comment comment = gson.fromJson(gson.toJson(document), Comment.class);
            comments.add(comment);
        }
        return comments;
    }

    public void updateComments(String title, List<Comment> comments){
        collection = collection.withCodecRegistry(pojoCodecRegistry);
        Bson update = new Document("comments", comments);
        Bson updateOperation = new Document("$set", update);
        collection.updateOne(new Document("title", title), updateOperation);
    }
}
