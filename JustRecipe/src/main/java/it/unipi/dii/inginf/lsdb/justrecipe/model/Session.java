package it.unipi.dii.inginf.lsdb.justrecipe.model;

import it.unipi.dii.inginf.lsdb.justrecipe.persistence.MongoDBDriver;
import it.unipi.dii.inginf.lsdb.justrecipe.persistence.Neo4jDriver;

public class Session {
    private User loggedUser;
    private Neo4jDriver neo4jDriver;
    private MongoDBDriver mongoDBDriver;

    public Session(User loggedUser, Neo4jDriver neo4jDriver, MongoDBDriver mongoDBDriver) {
        this.loggedUser = loggedUser;
        this.neo4jDriver = neo4jDriver;
        this.mongoDBDriver = mongoDBDriver;
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public Neo4jDriver getNeo4jDriver() {
        return neo4jDriver;
    }

    public MongoDBDriver getMongoDBDriver() {
        return mongoDBDriver;
    }
}
