package it.unipi.dii.inginf.lsdb.justrecipe.controller;

import it.unipi.dii.inginf.lsdb.justrecipe.persistence.MongoDBDriver;
import it.unipi.dii.inginf.lsdb.justrecipe.persistence.Neo4jDriver;

public class DiscoveryPageController {
    private Neo4jDriver neo4jDriver;
    private MongoDBDriver mongoDBDriver;
    private String username;

    public void initialize ()
    {
        neo4jDriver = Neo4jDriver.getInstance();
        //mongoDBDriver = MongoDBDriver.getInstance();
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
