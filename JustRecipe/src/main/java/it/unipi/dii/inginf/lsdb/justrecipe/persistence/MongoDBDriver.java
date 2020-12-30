package it.unipi.dii.inginf.lsdb.justrecipe.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import it.unipi.dii.inginf.lsdb.justrecipe.config.ConfigurationParameters;
import it.unipi.dii.inginf.lsdb.justrecipe.model.*;
import it.unipi.dii.inginf.lsdb.justrecipe.utils.Utils;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import static com.mongodb.client.model.Accumulators.addToSet;
import static com.mongodb.client.model.Accumulators.sum;

import javax.print.Doc;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
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
        chooseCollection("recipes");
    }

    /**
     * Method that inits the MongoClient and choose the correct database
     */
    @Override
    public boolean initConnection() {
        try
        {
            ConnectionString connectionString;

            if (!username.equals("")) // if there are access rules
            {
                connectionString = new ConnectionString("mongodb://" + username + ":" + password
                        + "@" + ip + ":" + port);
            } else // standard access
            {
                connectionString = new ConnectionString("mongodb://" + ip + ":" + port);
            }
            mongoClient = MongoClients.create(connectionString);
            database = mongoClient.getDatabase(dbName);
            pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                    fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        }
        catch (Exception ex)
        {
            System.err.println("MongoDB is not available");
            return false;
        }
        return true;
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
     * Edit an already present recipe
     * @param r the new recipe to replace the old one
     */
    public void editRecipe(Recipe r){
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
        doc.append("creationTime", r.getCreationTime())
                .append("authorUsername",r.getAuthorUsername());
        // Other option field
        if(r.getPicture() != null)
            doc.append("picture",r.getPicture());

        Bson updateOperation = new Document("$set", doc);
        collection.updateOne(new Document("title", r.getTitle()), updateOperation);

//        collection.replaceOne(Filters.eq("title", r.getTitle()), doc);
    }

    /**
     * Function that deletes the recipe from the database
     * @param recipe    Recipe to delete
     */
    public void deleteRecipe (Recipe recipe)
    {
        collection.deleteOne(eq("title", recipe.getTitle()));
    }

    /**
     * Function that deletes all the recipe of on user
     * @param username  Username of the user
     */
    public void deleteAllRecipesOfUser (String username)
    {
        collection.deleteMany(eq("authorUsername", username));
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
     * Function that returns a list of recipes that contains the category passed (or one piece of that)
     * @param category          Category to search
     * @param howManySkip       How many to skip
     * @param howMany           How many to obtain
     * @return                  The list of recipes
     */
    public List<Recipe> searchRecipesFromCategory (String category, int howManySkip, int howMany)
    {
        List<Recipe> recipes = new ArrayList<>();
        Gson gson = new Gson();
        Pattern pattern = Pattern.compile("^.*" + category + ".*$", Pattern.CASE_INSENSITIVE);
        Bson match = match(Filters.regex("categories", pattern));
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
     * Function that returns the recipes that contains the ingredients passed as argument
     * @param ingredients       Ingredients to check
     * @param howManySkip       How many recipe to skip
     * @param howMany           How many recipe to obtain
     * @return                  List of recipes
     */
    public List<Recipe> searchRecipesFromIngredients (List<String> ingredients, int howManySkip, int howMany)
    {
        List<Recipe> recipes = new ArrayList<>();
        Gson gson = new Gson();
        List<Bson> bsons = new ArrayList<Bson>();
        for (String ingredient: ingredients)
        {
            Pattern pattern = Pattern.compile("^.*" + ingredient + ".*$", Pattern.CASE_INSENSITIVE);
            bsons.add(match(Filters.regex("ingredients", pattern)));
        }
        bsons.add(sort(descending("creationTime")));
        bsons.add(skip(howManySkip));
        bsons.add(limit(howMany));
        List<Document> results = (List<Document>) collection.aggregate(bsons)
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
        Bson project = project(fields(computed("category", "$_id"), excludeId(), include("numberOfRecipes")));
        Bson sort = sort(descending("numberOfRecipes"));
        Bson skip = skip(howManySkip);
        Bson limit = limit(howManyCategories);
        List<Document> results = (List<Document>)
                collection.aggregate(Arrays.asList(unwind, group, project, sort, skip, limit)).into(new ArrayList());

        for (Document document: results)
        {
            mostCommonCategories.add(document.getString("category"));
        }
        return mostCommonCategories;
    }

    /**
     * Give the recipes in the db in the interval [howManyToSkip, howManyToGet+howManyToSkip]
     * @param howManyToSkip
     * @param howManyToGet
     * @return  The list of recipes
     */
    public List<Recipe> searchAllRecipes(int howManyToSkip, int howManyToGet)
    {
        List<Recipe> listOfRecipes = new ArrayList<>();
        Bson sort = sort(descending("creationTime"));
        Bson skip = skip(howManyToSkip);
        Bson limit = limit(howManyToGet);
        Bson proj = project(fields(excludeId(), include("title","instructions","ingredients","creationTime","authorUsername")));
        List<Document> results = (List<Document>) collection.aggregate(Arrays.asList(sort,skip,limit,proj)).into(new ArrayList());

        Type recipeListType = new TypeToken<ArrayList<Recipe>>(){}.getType();
        Gson gson = new Gson();
        listOfRecipes = gson.fromJson(gson.toJson(results), recipeListType);
        return listOfRecipes;
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
     * Function that returns a list of lists, each one composed by two object, the comment and the recipe which it is related on
     * @param howManySkip       How many comments to skip
     * @param howMany           How many comments to get
     * @return                  List of lists of object
     */
    public List<List<Object>> searchAllComments (int howManySkip, int howMany)
    {
        List<List<Object>> objects = new ArrayList<>();
        Gson gson = new Gson();
        Bson unwind = unwind("$comments");
        Bson sort = sort(descending("comments.creationTime"));
        Bson skip = skip(howManySkip);
        Bson limit = limit(howMany);
        MongoCursor<Document> iterator = (MongoCursor<Document>)
                collection.aggregate(Arrays.asList(unwind, sort, skip, limit)).iterator();
        while (iterator.hasNext())
        {
            Document document = iterator.next();
            Document commentDocument = (Document) document.get("comments");
            Comment comment = gson.fromJson(gson.toJson(commentDocument), Comment.class);
            // I need to re-obtain the full recipe
            Recipe recipe = getRecipeFromTitle(document.getString("title"));

            List<Object> objectList = new ArrayList<>();
            objectList.add(comment);
            objectList.add(recipe);

            objects.add(objectList);
        }
        return objects;
    }

    /**
     * Function who updates the comments field in recipe
     * @param title     recipe Title
     * @param comments  list of comments who will updates the recipe fields
     */
    public void updateComments(String title, List<Comment> comments){
        collection = collection.withCodecRegistry(pojoCodecRegistry);
        Bson update = new Document("comments", comments);
        Bson updateOperation = new Document("$set", update);
        collection.updateOne(new Document("title", title), updateOperation);
    }

    /**
     * Function who removes a comment element from a comment view and calls the updateComments to update
     * the recipe into mongo
     * @param recipe     recipe name to modify
     * @param comment   comment to delete
     */
    public void deleteComment(Recipe recipe, Comment comment){
        List<Comment> comments = recipe.getComments();
        int i=0;
        int k=0;
        for (Comment c: comments) {
            if(c.getCreationTime().equals(comment.getCreationTime()) &&
                    c.getAuthorUsername().equals(comment.getAuthorUsername())){
                k=i;
                break;
            }
            i++;
        }
        comments.remove(k);
        updateComments(recipe.getTitle(), comments);
    }

    /**
     * Gets all the comments of a recipe, modify the one who has to be changed and make an upload
     * @param recipe
     * @param comment
     */
    public void modifyComment(Recipe recipe, Comment comment){
        List<Comment> comments = recipe.getComments();
        int i=0;
        for (Comment c: comments
             ) {
            if(c.getAuthorUsername().equals(comment.getAuthorUsername()) && c.getCreationTime().equals(
                    comment.getCreationTime())){
                comments.set(i, comment);
                break;
            }
            i++;
        }
        updateComments(recipe.getTitle(), comments);
    }

    /**
     * Function who adds a comment element to a list, with all the others comments for the recipe then updates the list
     * calling the updateComments
     * @param recipe     recipe  to modify
     * @param comment   comment to add
     */
    public void addComment(Recipe recipe, Comment comment){
        if (recipe.getComments() == null)
            recipe.setComments(new ArrayList<>());
        List<Comment> comments = recipe.getComments();
        comments.add(comment);
        updateComments(recipe.getTitle(), comments);
    }

    /**
     * Function that returns the recipes of the week added by the user
     * @param username      Username of the user
     * @return              A list of recipes
     */
    public List<Recipe> getWeeklyRecipes (String username)
    {
        List<Recipe> recipes = new ArrayList<>();
        Gson gson = new Gson();
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.SUNDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0); // clear would not reset the hour of day
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        Date firstDateOfWeek = cal.getTime(); //First date: Sunday
        // start of the next week
        cal.add(Calendar.WEEK_OF_YEAR, 1);
        Date firstDateOfNextWeek = cal.getTime(); //First date of next week: next sunday

        Bson match = match(and(Filters.gte("creationTime", firstDateOfWeek),
                Filters.lt("creationTime", firstDateOfNextWeek),
                Filters.eq("authorUsername", username)));
        List<Document> results = (List<Document>)
                collection.aggregate(Arrays.asList(match)).into(new ArrayList());
        Type recipeListType = new TypeToken<ArrayList<Recipe>>(){}.getType();
        recipes = gson.fromJson(gson.toJson(results), recipeListType);
        return recipes;
    }

    public String mostVersatileUserRecipes(){
        List<Recipe> recipes = new ArrayList<>();
        Bson unwind = unwind("$categories");
        Bson group = new Document("$group", new Document("_id", new Document("author", "$authorUsername").append("category",
                "$categories")).append("numRecipe", new Document("$sum", 1)));
        Bson match = match(gte("numRecipe",5));
        Bson group2 = group("$_id.author", sum("distinctCategories", 1));
        Bson sort = sort(descending("distinctCategories"));
//        Bson skip = skip(howManySkip);
        Bson limit = limit(8);
        collection.aggregate(Arrays.asList(unwind, group, match, group2, sort, limit)).forEach(printDocuments);
        Object o = collection.aggregate(Arrays.asList(unwind, group, match, group2, sort, limit)).first();
        String mostVersatileUser = o.toString();
        mostVersatileUser=mostVersatileUser.substring(mostVersatileUser.indexOf("=")+1);
        mostVersatileUser=mostVersatileUser.substring(0, mostVersatileUser.indexOf(","));
        System.out.println(mostVersatileUser);
//        recipes = getRecipesFromAuthorUsername(howManySkip, howMany, mostVersatileUser);
        return mostVersatileUser;
    }
}
