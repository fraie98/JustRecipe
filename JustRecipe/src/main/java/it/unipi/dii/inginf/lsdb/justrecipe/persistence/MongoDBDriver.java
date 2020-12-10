package it.unipi.dii.inginf.lsdb.justrecipe.persistence;

import com.mongodb.ConnectionString;
import com.mongodb.client.*;
import it.unipi.dii.inginf.lsdb.justrecipe.config.ConfigurationParameters;
import it.unipi.dii.inginf.lsdb.justrecipe.model.Comment;
import it.unipi.dii.inginf.lsdb.justrecipe.model.Recipe;
import it.unipi.dii.inginf.lsdb.justrecipe.model.User;
import it.unipi.dii.inginf.lsdb.justrecipe.utils.Utils;
import org.bson.Document;
import org.bson.conversions.Bson;
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
     * @param from  first extreme of the interval
     * @param to    second extreme of the interval
     * @return      The list of (to-from) element to show, in descendent order from creationTime
     */
    // DA AGGIUNGERE: considerare solo le ricette fatte dagli utenti che seguo
    // Utilizzare la lista di follower
    public List<Recipe> getHomepageRecipe (int from, int to, List<User> follower)
    {
        List<Recipe> recipes = new ArrayList<>();
        Bson sort = sort(descending("creationTime"));
        Bson skip = skip(from);
        Bson limit = limit(to);
        MongoCursor cursor = collection.aggregate(Arrays.asList(sort, skip, limit)).iterator();
        while (cursor.hasNext())
        {
            Document doc = (Document) cursor.next();
            Recipe recipe = new Recipe(doc.getObjectId("_id"), doc.getString("title"),
                    doc.getString("instructions"), (List<String>)doc.get("ingredients"),
                    (List<String>)doc.get("categories"), doc.getInteger("calories", 0),
                    doc.getInteger("fat", 0), doc.getInteger("protein", 0),
                    doc.getInteger("carbs", 0), doc.getDate("creationTime"),
                    doc.getString("picture"), doc.getString("authorUsername"), new ArrayList<>());
            // I take also the comments information,
            // to avoid having to search for them when you see the single recipe
            if (doc.get("comments") != null)
            {
                List<Document> docCommentsArray = (ArrayList<Document>) doc.get("comments");
                List<Comment> comments = new ArrayList<>();
                for (Document docComment: docCommentsArray)
                {
                    Comment comment = new Comment(docComment.getString("author"),
                            docComment.getString("text"), docComment.getDate("creationTime"));
                    comments.add(comment);
                }
                recipe.setComments(comments);
            }
            recipes.add(recipe);
        }
        return recipes;
    }
}
