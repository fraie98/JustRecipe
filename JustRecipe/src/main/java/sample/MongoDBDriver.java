package sample;

import com.mongodb.ConnectionString;
import com.mongodb.client.*;
import org.bson.Document;

import java.util.function.Consumer;

/**
 * This class is used to communicate with MongoDB
 */
public class MongoDBDriver implements DatabaseDriver{
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection collection;
    private String hostname; //es: localhost
    private int port; // 27017 default value
    private String username;
    private String password;
    private final String DBNAME = "justrecipe";

    /**
     * Function that prints in json format all the document of a stream
     */
    private Consumer<Document> printDocuments = doc -> {
        System.out.println(doc.toJson());};

    //DA CAMBIARE: PASSARE LA CLASSE PARAMETRI DI CONFIGURAZIONE
    public MongoDBDriver (String hostName, int port, String username, String password)
    {
        this.hostname = hostName;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    /**
     * Method that inits the MongoClient and choose the correct database
     */
    @Override
    public void initConnection() {
        ConnectionString connectionString = new ConnectionString("mongodb://" + username + ":" + password
                + "@" + hostname + ":" + port);
        mongoClient = MongoClients.create(connectionString);
        database = mongoClient.getDatabase(DBNAME);
    }

    /**
     * Method used to close the connection
     */
    @Override
    public void closeConnection() {
        mongoClient.close();
    }

    /**
     * Method used to change the collection
     * @param name  name of the new collection
     */
    public void changeCollection(String name)
    {
        collection = database.getCollection(name);
    }
}
