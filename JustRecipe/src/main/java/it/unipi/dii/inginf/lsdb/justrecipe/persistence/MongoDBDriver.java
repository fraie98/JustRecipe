package it.unipi.dii.inginf.lsdb.justrecipe.persistence;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.ConnectionString;
import com.mongodb.client.*;
import it.unipi.dii.inginf.lsdb.justrecipe.config.ConfigurationParameters;
import it.unipi.dii.inginf.lsdb.justrecipe.model.Recipe;
import it.unipi.dii.inginf.lsdb.justrecipe.model.User;
import it.unipi.dii.inginf.lsdb.justrecipe.utils.Utils;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Consumer;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Sorts.descending;

/**
 * This class is used to communicate with MongoDB
 */
public class MongoDBDriver implements DatabaseDriver{
    private static MongoDBDriver instance;

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection collection;
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
     * Method used to change the collection
     * @param name  name of the new collection
     */
    public void chooseCollection(String name)
    {
        collection = database.getCollection(name);
    }

    /**
     * Function used to get the list of Recipe to show in the homepage
     * @param from      first extreme of the interval
     * @param to        second extreme of the interval
     * @param follower  list of follower of the user, used to choose the interest recipes
     * @return          The list of (to-from) element to show, in descendent order from creationTime
     */
    // DA AGGIUNGERE: considerare solo le ricette fatte dagli utenti che seguo
    // Utilizzare la lista di follower
    public List<Recipe> getHomepageRecipe (int from, int to, List<User> follower)
    {
        List<Recipe> recipes;
        Gson gson = new Gson();
        Bson sort = sort(descending("creationTime"));
        Bson skip = skip(from);
        Bson limit = limit(to);

        List<Document> results = (List<Document>) collection.aggregate(Arrays.asList(sort, skip, limit))
                .into(new ArrayList<>());
        // This is important for deserialize the results in a list of recipe
        Type recipeListType = new TypeToken<ArrayList<Recipe>>(){}.getType();
        recipes = gson.fromJson(gson.toJson(results), recipeListType);
        return recipes;
    }
}
