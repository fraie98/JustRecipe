package it.unipi.dii.inginf.lsdb.justrecipe.config;

/**
 * Class used to store the configuration parameters retrieved from the config.xml
 * There is no need to modify this value, so there are only the getters methods
 */
public class ConfigurationParameters {
    private String mongoIp;
    private int mongoPort;
    private String mongoUsername;
    private String mongoPassword;
    private String mongoDbName;
    private String neo4jIp;
    private int neo4jPort;
    private String neo4jUsername;
    private String neo4jPassword;

    public String getMongoIp() {
        return mongoIp;
    }

    public int getMongoPort() {
        return mongoPort;
    }

    public String getMongoUsername() {
        return mongoUsername;
    }

    public String getMongoPassword() {
        return mongoPassword;
    }

    public String getMongoDbName() {
        return mongoDbName;
    }

    public String getNeo4jIp() {
        return neo4jIp;
    }

    public int getNeo4jPort() {
        return neo4jPort;
    }

    public String getNeo4jUsername() {
        return neo4jUsername;
    }

    public String getNeo4jPassword() {
        return neo4jPassword;
    }
}
