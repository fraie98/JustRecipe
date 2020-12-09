package it.unipi.dii.inginf.lsdb.justrecipe.persistence;

import com.google.gson.Gson;
import com.mongodb.ConnectionString;
import com.mongodb.client.*;
import it.unipi.dii.inginf.lsdb.justrecipe.config.ConfigurationParameters;
import it.unipi.dii.inginf.lsdb.justrecipe.model.Recipe;
import it.unipi.dii.inginf.lsdb.justrecipe.utils.Utils;
import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

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

    // PER ORA METTO LE PRIME 20, PER PROVA
    public List<Recipe> getHomepageRecipe ()
    {
        List<Recipe> recipes = new ArrayList<>();
        Gson gson = new Gson();
        MongoCursor cursor = collection.find().limit(20).iterator();
        while (cursor.hasNext())
        {
            Document document = (Document) cursor.next();
            recipes.add(gson.fromJson(document.toJson(), Recipe.class));
        }
        for (Recipe recipe: recipes)
        {
            System.out.println(recipe.getTitle());
        }
        /*Document firstDoc = (Document) collection.find().first();
        System.out.println(firstDoc.toJson());
        Recipe recipe = gson.fromJson(firstDoc.toJson(), Recipe.class);
        System.out.println(recipe.getTitle());
        System.out.println(recipe.getCreationTime());*/
        return recipes;
    }

    /**
     * Method useful for checking development issues
     */
    public void printAllCollectionDocuments ()
    {
        collection.find().forEach(printDocuments);
    }
}
