package sample;

import com.mongodb.ConnectionString;
import com.mongodb.client.*;
import org.bson.Document;

import java.util.function.Consumer;

public class MongoDBDriver {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection collection;
    private String hostName; //es: localhost
    private int port; // 27017 default value
    private String username;
    private String password;

    private final String DBNAME = "justrecipe";

    private Consumer<Document> printDocuments = doc -> {
        System.out.println(doc.toJson());};

    //DA CAMBIARE: PASSARE LA CLASSE PARAMETRIDICONFIGURAZIONE
    public MongoDBDriver (String hostName, int port, String username, String password)
    {
        this.hostName = hostName;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public void initConnection()
    {
        ConnectionString connectionString = new ConnectionString("mongodb://" + username + ":" + password
                + "@" + hostName + ":" + port);
        mongoClient = MongoClients.create(connectionString);
        database = mongoClient.getDatabase(DBNAME);
    }

    public void changeCollections(String name)
    {
        collection = database.getCollection(name);
    }
}
